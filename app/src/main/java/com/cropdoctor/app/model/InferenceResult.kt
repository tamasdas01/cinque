/*
 * Data class holding the result of a single TFLite inference pass.
 */
package com.cropdoctor.app.model

data class InferenceResult(
    val rawLabel: String,    // e.g. "Tomato___Early_blight"
    val cropName: String,    // e.g. "Tomato"
    val diseaseName: String, // e.g. "Early Blight"
    val confidence: Float,   // 0.0f to 1.0f
    val isHealthy: Boolean   // true if label contains "healthy"
)
