package com.cropdoctor.app.data

import android.content.Context
import org.json.JSONArray

class ClassNameRepository(
    private val context: Context
) {

    val classNames: List<String> by lazy {
        loadClassNames()
    }

    // =====================================================
    // LOAD LABELS
    // =====================================================

    private fun loadClassNames(): List<String> {

        return try {

            val jsonString =
                context.assets
                    .open("class_names.json")
                    .bufferedReader()
                    .use { it.readText() }

            val jsonArray = JSONArray(jsonString)

            List(jsonArray.length()) { i ->
                jsonArray.getString(i)
            }

        } catch (e: Exception) {

            e.printStackTrace()

            emptyList()
        }
    }

    // =====================================================
    // FORMAT LABELS FOR UI
    // =====================================================

    fun formatLabel(
        raw: String
    ): Pair<String, String> {

        val parts = raw.split("___")

        val crop =
            parts.getOrElse(0) { raw }
                .replace("_", " ")
                .replace("(maize)", "")
                .trim()

        var disease =
            parts.getOrElse(1) { "" }
                .replace("_", " ")
                .replace("  ", " ")
                .trim()

        // Remove duplicate crop names
        // Example:
        // Apple -> Apple Scab
        disease = disease.replace(
            crop,
            "",
            ignoreCase = true
        ).trim()

        // Capitalize properly
        disease =
            disease.split(" ")
                .joinToString(" ") {
                    it.replaceFirstChar { c ->
                        c.uppercase()
                    }
                }

        return Pair(
            crop,
            disease.ifEmpty { "Healthy" }
        )
    }
}