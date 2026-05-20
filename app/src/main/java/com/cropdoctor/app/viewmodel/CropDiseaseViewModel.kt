/*
 * ViewModel that manages:
 * - model loading
 * - image selection
 * - inference
 * - UI state
 *
 * Stable version aligned with:
 * plant_disease_model_v7_clean.tflite
 */

package com.cropdoctor.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoctor.app.data.ClassNameRepository
import com.cropdoctor.app.model.InferenceResult
import com.cropdoctor.app.model.TFLiteInferenceHelper
import com.cropdoctor.app.utils.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =====================================================
// UI STATES
// =====================================================

sealed class DetectionState {

    object Idle : DetectionState()

    object ModelLoading : DetectionState()

    object ModelReady : DetectionState()

    object Analyzing : DetectionState()

    data class Success(
        val result: InferenceResult,
        val bitmap: Bitmap
    ) : DetectionState()

    data class Error(
        val message: String
    ) : DetectionState()
}

// =====================================================
// VIEWMODEL
// =====================================================

class CropDiseaseViewModel(
    application: Application
) : AndroidViewModel(application) {

    companion object {

        private const val TAG =
            "CropDiseaseVM"
    }

    // =====================================================
    // REPOSITORY + MODEL
    // =====================================================

    private val repository =
        ClassNameRepository(application)

    private val inferenceHelper =
        TFLiteInferenceHelper(
            application,
            repository
        )

    // =====================================================
    // SELECTED IMAGE
    // =====================================================

    private val _selectedBitmap =
        MutableStateFlow<Bitmap?>(null)

    val selectedBitmap:
            StateFlow<Bitmap?> =
        _selectedBitmap.asStateFlow()

    // =====================================================
    // DETECTION STATE
    // =====================================================

    private val _detectionState =
        MutableStateFlow<DetectionState>(
            DetectionState.ModelLoading
        )

    val detectionState:
            StateFlow<DetectionState> =
        _detectionState.asStateFlow()

    // =====================================================
    // INIT
    // =====================================================

    init {

        loadModel()
    }

    // =====================================================
    // LOAD MODEL
    // =====================================================

    private fun loadModel() {

        viewModelScope.launch {

            try {

                withContext(Dispatchers.IO) {

                    inferenceHelper.loadModel()
                }

                Log.d(
                    TAG,
                    "Model loaded successfully"
                )

                _detectionState.value =
                    DetectionState.ModelReady

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Model load failed",
                    e
                )

                _detectionState.value =
                    DetectionState.Error(
                        "Failed to load model:\n${e.message}"
                    )
            }
        }
    }

    // =====================================================
    // IMAGE SELECTION
    // =====================================================

    fun onImageSelected(
        uri: Uri
    ) {

        viewModelScope.launch {

            try {

                val bitmap =
                    withContext(Dispatchers.IO) {

                        BitmapUtils.uriToBitmap(
                            getApplication(),
                            uri
                        )
                    }

                if (bitmap != null) {

                    _selectedBitmap.value =
                        bitmap

                    _detectionState.value =
                        DetectionState.ModelReady

                    Log.d(
                        TAG,
                        """
                        Image loaded successfully
                        Width  : ${bitmap.width}
                        Height : ${bitmap.height}
                        """.trimIndent()
                    )

                } else {

                    Log.e(
                        TAG,
                        "Bitmap decode returned null"
                    )

                    _detectionState.value =
                        DetectionState.Error(
                            "Could not load image."
                        )
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Image selection failed",
                    e
                )

                _detectionState.value =
                    DetectionState.Error(
                        "Failed to process image:\n${e.message}"
                    )
            }
        }
    }

    // =====================================================
    // DETECT DISEASE
    // =====================================================

    fun detectDisease() {

        val bitmap =
            _selectedBitmap.value
                ?: run {

                    _detectionState.value =
                        DetectionState.Error(
                            "No image selected."
                        )

                    return
                }

        viewModelScope.launch {

            try {

                _detectionState.value =
                    DetectionState.Analyzing

                val result =
                    withContext(Dispatchers.Default) {

                        inferenceHelper.classify(
                            bitmap
                        )
                    }

                Log.d(
                    TAG,
                    """
                    Detection Success
                    Crop       : ${result.cropName}
                    Disease    : ${result.diseaseName}
                    Confidence : ${(result.confidence * 100).toInt()}%
                    """.trimIndent()
                )

                _detectionState.value =
                    DetectionState.Success(
                        result,
                        bitmap
                    )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Detection failed",
                    e
                )

                _detectionState.value =
                    DetectionState.Error(
                        "Detection failed:\n${e.message}"
                    )
            }
        }
    }

    // =====================================================
    // RESET
    // =====================================================

    fun reset() {

        _selectedBitmap.value = null

        _detectionState.value =
            DetectionState.ModelReady
    }

    // =====================================================
    // CLEAR ERROR
    // =====================================================

    fun clearError() {

        if (
            _detectionState.value
            is DetectionState.Error
        ) {

            _detectionState.value =
                DetectionState.ModelReady
        }
    }

    // =====================================================
    // CLEANUP
    // =====================================================

    override fun onCleared() {

        super.onCleared()

        inferenceHelper.close()
    }
}