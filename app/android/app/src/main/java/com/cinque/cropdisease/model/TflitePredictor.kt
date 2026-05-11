package com.cinque.cropdisease.model

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TflitePredictor(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private var inputWidth = 224
    private var inputHeight = 224
    private var inputType = DataType.FLOAT32
    private var outputType = DataType.FLOAT32
    private var outputSize = 0

    val inputWidthSize: Int
        get() = inputWidth

    val inputHeightSize: Int
        get() = inputHeight

    val labelsCount: Int
        get() = labels.size

    @Synchronized
    fun ensureLoaded() {
        if (interpreter != null) return

        labels = LabelStore(context).loadLabels()
        val modelBuffer = loadModelFile("model.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        val localInterpreter = Interpreter(modelBuffer, options)
        interpreter = localInterpreter

        val inputTensor = localInterpreter.getInputTensor(0)
        val inputShape = inputTensor.shape()
        if (inputShape.size >= 3) {
            inputHeight = inputShape[1]
            inputWidth = inputShape[2]
        }
        inputType = inputTensor.dataType()

        val outputTensor = localInterpreter.getOutputTensor(0)
        outputType = outputTensor.dataType()
        val outputShape = outputTensor.shape()
        outputSize = if (outputShape.isNotEmpty()) outputShape.last() else 0
    }

    fun predict(base64: String): PredictionResult {
        ensureLoaded()
        val localInterpreter = interpreter ?: throw IllegalStateException("Model not loaded.")
        if (outputSize <= 0) {
            throw IllegalStateException("Invalid output tensor shape.")
        }

        val cleanBase64 = base64.substringAfter(",")
        val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IllegalArgumentException("Invalid image data.")

        val resized = android.graphics.Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        val inputBuffer = ImagePreprocessor.toByteBuffer(resized, inputType)

        val confidenceScores = when (outputType) {
            DataType.FLOAT32 -> {
                val output = Array(1) { FloatArray(outputSize) }
                localInterpreter.run(inputBuffer, output)
                output[0]
            }
            DataType.UINT8 -> {
                val output = Array(1) { ByteArray(outputSize) }
                localInterpreter.run(inputBuffer, output)
                val scores = FloatArray(outputSize)
                for (i in 0 until outputSize) {
                    scores[i] = (output[0][i].toInt() and 0xFF) / 255f
                }
                scores
            }
            else -> {
                val output = Array(1) { FloatArray(outputSize) }
                localInterpreter.run(inputBuffer, output)
                output[0]
            }
        }

        var bestIndex = 0
        var bestScore = confidenceScores[0]
        for (i in 1 until confidenceScores.size) {
            if (confidenceScores[i] > bestScore) {
                bestScore = confidenceScores[i]
                bestIndex = i
            }
        }

        val label = if (bestIndex < labels.size) labels[bestIndex] else "Unknown"
        return PredictionResult(label, bestScore, bestIndex)
    }

    private fun loadModelFile(assetName: String): MappedByteBuffer {
        context.assets.openFd(assetName).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }
}
