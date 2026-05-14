package com.cropdoctor.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream
import kotlin.math.max

object BitmapUtils {

    // =====================================================
    // URI -> BITMAP
    // MEMORY SAFE
    // =====================================================

    fun uriToBitmap(
        context: Context,
        uri: Uri
    ): Bitmap? {

        return try {

            // ---------------------------------------------
            // READ IMAGE BOUNDS ONLY
            // ---------------------------------------------

            val boundsOptions =
                BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

            context.contentResolver
                .openInputStream(uri)
                ?.use { stream ->

                    BitmapFactory.decodeStream(
                        stream,
                        null,
                        boundsOptions
                    )
                }

            // ---------------------------------------------
            // CALCULATE SAMPLE SIZE
            // ---------------------------------------------

            val maxDimension = 1024

            var sampleSize = 1

            while (
                boundsOptions.outWidth / sampleSize > maxDimension ||
                boundsOptions.outHeight / sampleSize > maxDimension
            ) {
                sampleSize *= 2
            }

            // ---------------------------------------------
            // DECODE SCALED IMAGE
            // ---------------------------------------------

            val decodeOptions =
                BitmapFactory.Options().apply {

                    inSampleSize = sampleSize

                    inPreferredConfig =
                        Bitmap.Config.ARGB_8888
                }

            val bitmap =
                context.contentResolver
                    .openInputStream(uri)
                    ?.use { stream ->

                        BitmapFactory.decodeStream(
                            stream,
                            null,
                            decodeOptions
                        )
                    }
                    ?: return null

            // ---------------------------------------------
            // FIX ROTATION
            // ---------------------------------------------

            val exif =
                context.contentResolver
                    .openInputStream(uri)
                    ?.use {
                        ExifInterface(it)
                    }

            rotateBitmapIfRequired(
                bitmap,
                exif
            )

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    // =====================================================
    // ROTATION FIX
    // =====================================================

    private fun rotateBitmapIfRequired(
        bitmap: Bitmap,
        exif: ExifInterface?
    ): Bitmap {

        val orientation =
            exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
                ?: ExifInterface.ORIENTATION_NORMAL

        val matrix = Matrix()

        when (orientation) {

            ExifInterface.ORIENTATION_ROTATE_90 ->
                matrix.postRotate(90f)

            ExifInterface.ORIENTATION_ROTATE_180 ->
                matrix.postRotate(180f)

            ExifInterface.ORIENTATION_ROTATE_270 ->
                matrix.postRotate(270f)

            else -> return bitmap
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    // =====================================================
    // FINAL MODEL RESIZE
    // =====================================================

    fun resizeForModel(
        bitmap: Bitmap,
        size: Int = 384
    ): Bitmap {

        return Bitmap.createScaledBitmap(
            bitmap,
            size,
            size,
            true
        )
    }
}