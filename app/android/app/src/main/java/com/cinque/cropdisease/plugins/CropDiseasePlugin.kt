package com.cinque.cropdisease.plugins

import com.cinque.cropdisease.model.TflitePredictor
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import java.util.concurrent.Executors

@CapacitorPlugin(name = "CropDisease")
class CropDiseasePlugin : Plugin() {
    private val predictor by lazy { TflitePredictor(context) }
    private val executor = Executors.newSingleThreadExecutor()

    @PluginMethod
    fun load(call: PluginCall) {
        // Run model loading off the main thread
        executor.execute {
            try {
                predictor.ensureLoaded()
                val result = JSObject()
                result.put("inputWidth", predictor.inputWidthSize)
                result.put("inputHeight", predictor.inputHeightSize)
                result.put("labelsCount", predictor.labelsCount)
                call.resolve(result)
            } catch (ex: Exception) {
                call.reject(ex.message ?: "Failed to load model", ex)
            }
        }
    }

    @PluginMethod
    fun predict(call: PluginCall) {
        val base64 = call.getString("base64")
        if (base64.isNullOrBlank()) {
            call.reject("Missing base64 image data")
            return
        }
        // Run inference off the main thread so the UI stays responsive
        executor.execute {
            try {
                val prediction = predictor.predict(base64)
                val result = JSObject()
                result.put("label", prediction.label)
                result.put("confidence", prediction.confidence)
                result.put("index", prediction.index)
                call.resolve(result)
            } catch (ex: Exception) {
                call.reject(ex.message ?: "Prediction failed", ex)
            }
        }
    }
}
