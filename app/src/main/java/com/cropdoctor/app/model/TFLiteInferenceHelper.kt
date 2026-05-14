package com.cropdoctor.app.model

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.cropdoctor.app.data.ClassNameRepository
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.min

class TFLiteInferenceHelper(
    private val context: Context,
    private val repository: ClassNameRepository
) {

    companion object {
        private const val TAG = "TFLiteHelper"

        // MUST MATCH TRAINING
        private const val IMAGE_SIZE = 384

        private const val CHANNELS = 3
        private const val BYTES_PER_FLOAT = 4

        // Confidence threshold
        private const val MIN_CONFIDENCE = 0.60f

        // Top predictions to keep
        private const val TOP_K = 3
    }

    private var interpreter: Interpreter? = null

    // =====================================================
    // LOAD MODEL
    // =====================================================

    fun loadModel() {

        val options = Interpreter.Options().apply {

            numThreads = 4

            // Optional:
            // setUseNNAPI(true)

            // Optional GPU delegate later
        }

        interpreter = Interpreter(
            loadModelFile(),
            options
        )

        Log.d(TAG, "Model loaded successfully")
    }

    // =====================================================
    // LOAD MODEL FILE
    // =====================================================

    private fun loadModelFile(): MappedByteBuffer {

        val fileDescriptor =
            context.assets.openFd("model_v6.tflite")

        val inputStream =
            FileInputStream(fileDescriptor.fileDescriptor)

        val fileChannel = inputStream.channel

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    // =====================================================
    // MAIN INFERENCE
    // =====================================================

    fun classify(bitmap: Bitmap): InferenceResult {

        val interpreter = interpreter
            ?: throw IllegalStateException(
                "Model not loaded."
            )

        val startTime = SystemClock.uptimeMillis()

        // Resize
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            IMAGE_SIZE,
            IMAGE_SIZE,
            true
        )

        // Input tensor
        val inputBuffer = ByteBuffer.allocateDirect(
            1 *
                    IMAGE_SIZE *
                    IMAGE_SIZE *
                    CHANNELS *
                    BYTES_PER_FLOAT
        ).apply {
            order(ByteOrder.nativeOrder())
        }

        // =================================================
        // EFFICIENTNETV2 PREPROCESSING
        // SAME AS:
        // preprocess_input()
        // =================================================

        for (y in 0 until IMAGE_SIZE) {

            for (x in 0 until IMAGE_SIZE) {

                val pixel = resizedBitmap.getPixel(x, y)

                val r = ((pixel shr 16) and 0xFF)
                val g = ((pixel shr 8) and 0xFF)
                val b = (pixel and 0xFF)

                // Convert to [-1, 1]

                inputBuffer.putFloat(
                    (r / 127.5f) - 1f
                )

                inputBuffer.putFloat(
                    (g / 127.5f) - 1f
                )

                inputBuffer.putFloat(
                    (b / 127.5f) - 1f
                )
            }
        }

        inputBuffer.rewind()

        // =================================================
        // OUTPUT BUFFER
        // =================================================

        val numClasses = repository.classNames.size

        val output =
            Array(1) { FloatArray(numClasses) }

        // Run inference
        interpreter.run(inputBuffer, output)

        val probabilities = output[0]

        // =================================================
        // TOP-K PREDICTIONS
        // =================================================

        val topPredictions =
            probabilities.indices
                .sortedByDescending { probabilities[it] }
                .take(min(TOP_K, probabilities.size))

        val bestIndex = topPredictions.first()

        val bestConfidence =
            probabilities[bestIndex]

        val rawLabel =
            repository.classNames.getOrElse(bestIndex) {
                "Unknown"
            }

        val (crop, disease) =
            repository.formatLabel(rawLabel)

        val inferenceTime =
            SystemClock.uptimeMillis() - startTime

        // =================================================
        // DEBUG LOGGING
        // =================================================

        Log.d(TAG, "========== PREDICTIONS ==========")

        topPredictions.forEachIndexed { rank, index ->

            Log.d(
                TAG,
                "#${rank + 1} " +
                        "${repository.classNames[index]} " +
                        "- ${(probabilities[index] * 100f)}%"
            )
        }

        Log.d(
            TAG,
            "Inference time: ${inferenceTime} ms"
        )

        // =================================================
        // CONFIDENCE THRESHOLDING
        // =================================================

        val finalDisease =
            if (bestConfidence < MIN_CONFIDENCE) {
                "Uncertain Disease"
            } else {
                disease.ifEmpty { "Unknown" }
            }

        return InferenceResult(
            rawLabel = rawLabel,
            cropName = crop,
            diseaseName = finalDisease,
            confidence = bestConfidence,
            isHealthy = rawLabel.contains(
                "healthy",
                ignoreCase = true
            )
        )
    }

    // =====================================================
    // CLEANUP
    // =====================================================

    fun close() {

        interpreter?.close()

        interpreter = null

        Log.d(TAG, "Interpreter closed")
    }
}