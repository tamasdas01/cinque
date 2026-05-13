/*
 * ViewModel that manages state for the crop disease detection workflow.
 * Survives configuration changes and exposes StateFlow to Compose UI.
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

// Sealed class for all possible UI states
sealed class DetectionState {
    object Idle : DetectionState()
    object ModelLoading : DetectionState()
    object ModelReady : DetectionState()
    object Analyzing : DetectionState()
    data class Success(val result: InferenceResult, val bitmap: Bitmap) : DetectionState()
    data class Error(val message: String) : DetectionState()
}

class CropDiseaseViewModel(application: Application) : AndroidViewModel(application) {

    private val logTag = "CropDoctor"

    private val repository = ClassNameRepository(application)
    private val inferenceHelper = TFLiteInferenceHelper(application, repository)

    // Currently selected image bitmap for preview
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    // Main detection state
    private val _detectionState = MutableStateFlow<DetectionState>(DetectionState.ModelLoading)
    val detectionState: StateFlow<DetectionState> = _detectionState.asStateFlow()

    init {
        loadModel()
    }

    /** Loads the TFLite model asynchronously in a background thread */
    private fun loadModel() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    inferenceHelper.loadModel()
                }
                _detectionState.value = DetectionState.ModelReady
            } catch (e: Exception) {
                Log.e(logTag, "Model load failed", e)
                _detectionState.value = DetectionState.Error("Failed to load model: ${e.message}")
            }
        }
    }

    /**
     * Called when user selects an image (from camera or gallery).
     * Loads and compresses the bitmap for preview.
     */
    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                val raw = BitmapUtils.uriToBitmap(getApplication(), uri)
                raw?.let { BitmapUtils.compressBitmap(it) }
            }
            if (bitmap != null) {
                _selectedBitmap.value = bitmap
                _detectionState.value = DetectionState.ModelReady
            } else {
                Log.e(logTag, "Bitmap decode failed for uri: $uri")
                _detectionState.value = DetectionState.Error("Could not load image. Please try another.")
            }
        }
    }

    /**
     * Runs TFLite inference on the currently selected bitmap.
     */
    fun detectDisease() {
        val bitmap = _selectedBitmap.value ?: return
        viewModelScope.launch {
            _detectionState.value = DetectionState.Analyzing
            try {
                val result = withContext(Dispatchers.Default) {
                    inferenceHelper.classify(bitmap)
                }
                _detectionState.value = DetectionState.Success(result, bitmap)
            } catch (e: Exception) {
                Log.e(logTag, "Detection failed", e)
                _detectionState.value = DetectionState.Error("Detection failed: ${e.message}")
            }
        }
    }

    /** Resets state so user can try another image */
    fun reset() {
        _selectedBitmap.value = null
        _detectionState.value = DetectionState.ModelReady
    }

    /** Clears error state while keeping the current bitmap, if any */
    fun clearError() {
        if (_detectionState.value is DetectionState.Error) {
            _detectionState.value = DetectionState.ModelReady
        }
    }

    override fun onCleared() {
        super.onCleared()
        inferenceHelper.close()
    }
}
