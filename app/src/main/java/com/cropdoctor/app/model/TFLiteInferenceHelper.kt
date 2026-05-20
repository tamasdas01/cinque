/*
 * Loads clean TFLite model from assets,
 * preprocesses Bitmap correctly,
 * runs inference,
 * and returns ranked prediction results.
 *
 * IMPORTANT:
 * This version is aligned with:
 * plant_disease_model_v7_clean.tflite
 */

package com.cropdoctor.app.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.cropdoctor.app.data.ClassNameRepository
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteInferenceHelper(
    private val context: Context,
    private val repository: ClassNameRepository
) {

    companion object {
        private const val TAG = "TFLiteHelper"
    }

    // =====================================================
    // MODEL CONFIG
    // =====================================================

    private val imageSize = 384

    private val channels = 3

    private val bytesPerFloat = 4

    private var interpreter: Interpreter? = null

    // =====================================================
    // LOAD MODEL
    // =====================================================

    fun loadModel() {

        if (interpreter != null) {

            Log.d(TAG, "Model already loaded")

            return
        }

        try {

            val options = Interpreter.Options().apply {

                // Stable CPU inference
                numThreads = 4

                // XNNPACK acceleration
                setUseXNNPACK(true)
            }

            interpreter = Interpreter(
                loadModelFile(),
                options
            )

            Log.d(
                TAG,
                "TFLite model loaded successfully"
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to load TFLite model",
                e
            )

            interpreter = null

            throw e
        }
    }

    // =====================================================
    // LOAD MODEL FILE
    // =====================================================

    private fun loadModelFile(): MappedByteBuffer {

        val fileDescriptor =
            context.assets.openFd(
                "plant_disease_model_v7_clean.tflite"
            )

        val inputStream =
            FileInputStream(
                fileDescriptor.fileDescriptor
            )

        val fileChannel =
            inputStream.channel

        val startOffset =
            fileDescriptor.startOffset

        val declaredLength =
            fileDescriptor.declaredLength

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    // =====================================================
    // MAIN INFERENCE
    // =====================================================

    fun classify(
        bitmap: Bitmap
    ): InferenceResult {

        val localInterpreter =
            interpreter
                ?: throw IllegalStateException(
                    "Model not loaded. Call loadModel() first."
                )

        // -------------------------------------------------
        // RESIZE IMAGE
        // -------------------------------------------------

        val resizedBitmap =
            Bitmap.createScaledBitmap(
                bitmap,
                imageSize,
                imageSize,
                true
            )

        // -------------------------------------------------
        // CREATE INPUT BUFFER
        // Shape:
        // [1, 384, 384, 3]
        // -------------------------------------------------

        val inputBuffer =
            ByteBuffer.allocateDirect(
                1 *
                        imageSize *
                        imageSize *
                        channels *
                        bytesPerFloat
            ).apply {

                order(ByteOrder.nativeOrder())
            }

        // -------------------------------------------------
        // PREPROCESSING
        //
        // IMPORTANT:
        // CLEAN MODEL EXPECTS:
        //
        // image / 255.0f
        //
        // NOT:
        // (x / 127.5f) - 1f
        // -------------------------------------------------

        for (y in 0 until imageSize) {

            for (x in 0 until imageSize) {

                val pixel =
                    resizedBitmap.getPixel(x, y)

                val r = ((pixel shr 16) and 0xFF).toFloat()

                val g = ((pixel shr 8) and 0xFF).toFloat()

                val b = (pixel and 0xFF).toFloat()

                inputBuffer.putFloat(r)

                inputBuffer.putFloat(g)

                inputBuffer.putFloat(b)
            }
        }

        inputBuffer.rewind()

        // -------------------------------------------------
        // OUTPUT BUFFER
        // -------------------------------------------------

        val numClasses =
            repository.classNames.size

        if (numClasses <= 0) {

            throw IllegalStateException(
                "class_names_v7.json missing or empty."
            )
        }

        val outputBuffer =
            Array(1) {
                FloatArray(numClasses)
            }

        // -------------------------------------------------
        // RUN INFERENCE
        // -------------------------------------------------

        localInterpreter.run(
            inputBuffer,
            outputBuffer
        )

        val probabilities =
            outputBuffer[0]

        // -------------------------------------------------
        // GET BEST PREDICTION
        // -------------------------------------------------

        val maxIndex =
            probabilities.indices.maxByOrNull {
                probabilities[it]
            } ?: 0

        val confidence =
            probabilities[maxIndex]

        val rawLabel =
            repository.classNames.getOrElse(
                maxIndex
            ) {
                "Unknown"
            }

        val (crop, disease) =
            repository.formatLabel(rawLabel)

        // -------------------------------------------------
        // DEBUG LOGGING
        // -------------------------------------------------

        Log.d(
            TAG,
            """
            Prediction Complete
            Label      : $rawLabel
            Confidence : ${(confidence * 100).toInt()}%
            """.trimIndent()
        )

        // -------------------------------------------------
        // RETURN RESULT
        // -------------------------------------------------

        return InferenceResult(

            rawLabel = rawLabel,

            cropName = crop,

            diseaseName =
                if (disease.isBlank())
                    "Unknown"
                else
                    disease,

            confidence = confidence,

            isHealthy =
                rawLabel.contains(
                    "healthy",
                    ignoreCase = true
                )
        )
    }

    // =====================================================
    // RELEASE RESOURCES
    // =====================================================

    fun close() {

        interpreter?.close()

        interpreter = null
    }
}