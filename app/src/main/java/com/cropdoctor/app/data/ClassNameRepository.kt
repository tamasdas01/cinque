package com.cropdoctor.app.data

import android.content.Context
import android.util.Log
import org.json.JSONArray

class ClassNameRepository(
    private val context: Context
) {

    companion object {

        private const val TAG =
            "ClassNameRepository"
    }

    // =====================================================
    // CLASS NAMES
    // =====================================================

    val classNames: List<String> by lazy {

        loadClassNames()
    }

    // =====================================================
    // LOAD JSON LABELS
    // =====================================================

    private fun loadClassNames(): List<String> {

        return try {

            val jsonString =
                context.assets
                    .open("class_names_v7.json")
                    .bufferedReader()
                    .use { it.readText() }

            val jsonArray =
                JSONArray(jsonString)

            val labels =
                List(jsonArray.length()) { index ->

                    jsonArray.getString(index)
                }

            Log.d(
                TAG,
                "Loaded ${labels.size} class labels"
            )

            labels.forEachIndexed { index, label ->

                Log.d(
                    TAG,
                    "$index -> $label"
                )
            }

            labels

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to load class names",
                e
            )

            emptyList()
        }
    }

    // =====================================================
    // FORMAT LABEL FOR UI
    // =====================================================

    fun formatLabel(
        raw: String
    ): Pair<String, String> {

        return try {

            val parts =
                raw.split("___")

            // -------------------------------------------------
            // CROP
            // -------------------------------------------------

            val crop =
                parts.getOrElse(0) { raw }
                    .replace("_", " ")
                    .replace("(maize)", "")
                    .trim()
                    .split(" ")
                    .joinToString(" ") {

                        it.replaceFirstChar { char ->

                            char.uppercase()
                        }
                    }

            // -------------------------------------------------
            // DISEASE
            // -------------------------------------------------

            var disease =
                parts.getOrElse(1) { "Healthy" }
                    .replace("_", " ")
                    .replace("  ", " ")
                    .trim()

            // Remove duplicated crop names
            disease =
                disease.replace(
                    crop,
                    "",
                    ignoreCase = true
                ).trim()

            // Capitalize nicely
            disease =
                disease.split(" ")
                    .joinToString(" ") {

                        it.replaceFirstChar { char ->

                            char.uppercase()
                        }
                    }

            if (disease.isBlank()) {

                disease = "Healthy"
            }

            Pair(
                crop,
                disease
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Label formatting failed",
                e
            )

            Pair(
                "Unknown",
                "Unknown"
            )
        }
    }
}