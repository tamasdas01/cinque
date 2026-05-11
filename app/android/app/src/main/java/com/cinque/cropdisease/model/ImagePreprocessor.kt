package com.cinque.cropdisease.model

import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ImagePreprocessor {
    fun toByteBuffer(bitmap: Bitmap, inputType: DataType): ByteBuffer {
        val width = bitmap.width
        val height = bitmap.height
        val pixelCount = width * height
        val buffer = when (inputType) {
            DataType.FLOAT32 -> ByteBuffer.allocateDirect(4 * pixelCount * 3)
            DataType.UINT8 -> ByteBuffer.allocateDirect(pixelCount * 3)
            else -> ByteBuffer.allocateDirect(4 * pixelCount * 3)
        }.order(ByteOrder.nativeOrder())

        val pixels = IntArray(pixelCount)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var index = 0
        while (index < pixelCount) {
            val color = pixels[index]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF

            if (inputType == DataType.UINT8) {
                buffer.put(r.toByte())
                buffer.put(g.toByte())
                buffer.put(b.toByte())
            } else {
                buffer.putFloat(r / 255f)
                buffer.putFloat(g / 255f)
                buffer.putFloat(b / 255f)
            }
            index++
        }

        buffer.rewind()
        return buffer
    }
}
