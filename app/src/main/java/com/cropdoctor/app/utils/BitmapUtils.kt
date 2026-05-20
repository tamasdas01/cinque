/*
 * Bitmap utility functions
 * for safe image loading,
 * resizing,
 * and EXIF rotation correction.
 */

package com.cropdoctor.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface

object BitmapUtils {

    private const val TAG = "BitmapUtils"

    // =====================================================
    // URI -> BITMAP
    // MEMORY SAFE DECODING
    // =====================================================

    fun uriToBitmap(
        context: Context,
        uri: Uri
    ): Bitmap? {

        return try {

            // -------------------------------------------------
            // STEP 1
            // READ IMAGE DIMENSIONS ONLY
            // -------------------------------------------------

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

            val originalWidth =
                boundsOptions.outWidth

            val originalHeight =
                boundsOptions.outHeight

            Log.d(
                TAG,
                """
                Original Image
                Width  : $originalWidth
                Height : $originalHeight
                """.trimIndent()
            )

            // -------------------------------------------------
            // STEP 2
            // CALCULATE SAFE SAMPLE SIZE
            // -------------------------------------------------

            val maxDimension = 2048

            var sampleSize = 1

            while (
                originalWidth / sampleSize > maxDimension ||
                originalHeight / sampleSize > maxDimension
            ) {

                sampleSize *= 2
            }

            Log.d(
                TAG,
                "Sample Size: $sampleSize"
            )

            // -------------------------------------------------
            // STEP 3
            // DECODE ACTUAL BITMAP
            // -------------------------------------------------

            val decodeOptions =
                BitmapFactory.Options().apply {

                    inSampleSize = sampleSize

                    inPreferredConfig =
                        Bitmap.Config.ARGB_8888

                    inMutable = false
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

            if (bitmap == null) {

                Log.e(
                    TAG,
                    "Bitmap decoding failed"
                )

                return null
            }

            // -------------------------------------------------
            // STEP 4
            // FIX EXIF ROTATION
            // -------------------------------------------------

            val exif =
                context.contentResolver
                    .openInputStream(uri)
                    ?.use { stream ->

                        ExifInterface(stream)
                    }

            val rotatedBitmap =
                rotateBitmapIfRequired(
                    bitmap,
                    exif
                )

            Log.d(
                TAG,
                """
                Final Bitmap
                Width  : ${rotatedBitmap.width}
                Height : ${rotatedBitmap.height}
                """.trimIndent()
            )

            rotatedBitmap

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to decode bitmap",
                e
            )

            null
        }
    }

    // =====================================================
    // FIX ROTATION USING EXIF
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

            ExifInterface.ORIENTATION_ROTATE_90 -> {

                matrix.postRotate(90f)
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {

                matrix.postRotate(180f)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {

                matrix.postRotate(270f)
            }

            else -> {

                return bitmap
            }
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
    // RESIZE FOR MODEL
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