package com.cinque.cropdisease.model

data class PredictionResult(
    val label: String,
    val confidence: Float,
    val index: Int
)
