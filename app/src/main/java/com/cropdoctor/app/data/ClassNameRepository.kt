/*
 * Reads class_names.json from assets and provides label lookup.
 */
package com.cropdoctor.app.data

import android.content.Context
import org.json.JSONArray

class ClassNameRepository(private val context: Context) {

    // Lazy-loaded list of class name strings from assets/class_names.json
    val classNames: List<String> by lazy {
        loadClassNames()
    }

    private fun loadClassNames(): List<String> {
        return try {
            val jsonString = context.assets.open("class_names.json")
                .bufferedReader()
                .use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            List(jsonArray.length()) { i -> jsonArray.getString(i) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Formats a raw label like "Tomato___Early_blight" into
     * "Tomato - Early Blight" for display.
     */
    fun formatLabel(raw: String): Pair<String, String> {
        val parts = raw.split("___")
        val crop = parts.getOrElse(0) { raw }.replace("_", " ").trim()
        val disease = parts.getOrElse(1) { "" }.replace("_", " ").trim()
        return Pair(crop, disease)
    }
}
