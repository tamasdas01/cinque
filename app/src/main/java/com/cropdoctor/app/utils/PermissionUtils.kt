/*
 * Centralizes permission logic for Camera and Storage access.
 */

// PermissionUtils.kt
package com.cropdoctor.app.utils

import android.os.Build

object PermissionUtils {

    /**
     * Returns the correct list of permissions needed based on Android version.
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
}
