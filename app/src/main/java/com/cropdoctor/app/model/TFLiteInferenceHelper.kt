/*
 * Loads Model_V5.tflite from assets, preprocesses a Bitmap,
 * runs inference, and returns a ranked InferenceResult.
 */
package com.cropdoctor.app.model

import android.content.Context
import android.graphics.Bitmap
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

    // TFLite model input image size (must match model training config)
    private val imageSize = 224
    // Number of channels: RGB
    private val channels = 3
    // Bytes per float
    private val bytesPerFloat = 4

    private var interpreter: Interpreter? = null

    /**
     * Loads the TFLite model from assets into a MappedByteBuffer.
     * Call this once before running inference.
     */
    fun loadModel() {
        val options = Interpreter.Options().apply {
            numThreads = 4
        }
        interpreter = Interpreter(loadModelFile(), options)
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd("Model_V5.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Runs inference on a given Bitmap.
     * @param bitmap The input image (will be resized internally)
     * @return InferenceResult with top prediction and confidence
     */
    fun classify(bitmap: Bitmap): InferenceResult {
        val interpreter = interpreter
            ?: throw IllegalStateException("Model not loaded. Call loadModel() first.")

        // Resize bitmap to model input size
        val resized = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)

        // Prepare input ByteBuffer: shape [1, 224, 224, 3], normalized to [0,1]
        val inputBuffer = ByteBuffer.allocateDirect(
            1 * imageSize * imageSize * channels * bytesPerFloat
        ).apply { order(ByteOrder.nativeOrder()) }

        for (y in 0 until imageSize) {
            for (x in 0 until imageSize) {
                val pixel = resized.getPixel(x, y)
                // Normalize RGB to [0, 1]
                inputBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
                inputBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
                inputBuffer.putFloat((pixel and 0xFF) / 255.0f)
            }
        }

        // Output buffer: [1, NUM_CLASSES]
        val numClasses = repository.classNames.size
        if (numClasses <= 0) {
            throw IllegalStateException("class_names.json is empty or missing.")
        }
        val outputBuffer = Array(1) { FloatArray(numClasses) }

        // Run inference
        inputBuffer.rewind()
        interpreter.run(inputBuffer, outputBuffer)

        // Find top prediction
        val probabilities = outputBuffer[0]
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val confidence = probabilities[maxIndex]
        val rawLabel = repository.classNames.getOrElse(maxIndex) { "Unknown" }
        val (crop, disease) = repository.formatLabel(rawLabel)

        return InferenceResult(
            rawLabel = rawLabel,
            cropName = crop,
            diseaseName = if (disease.isEmpty()) "Unknown" else disease,
            confidence = confidence,
            isHealthy = rawLabel.contains("healthy", ignoreCase = true)
        )
    }

    /** Release interpreter resources */
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
