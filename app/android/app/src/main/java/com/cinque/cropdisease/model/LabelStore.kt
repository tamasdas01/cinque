package com.cinque.cropdisease.model

import android.content.Context
import org.json.JSONArray

class LabelStore(private val context: Context) {
    fun loadLabels(): List<String> {
        val fromLabels = readLines("labels.txt")
        if (fromLabels.isNotEmpty()) {
            return fromLabels
        }
        val json = readText("class_names.json")
        if (json.isNotBlank()) {
            return parseJsonLabels(json)
        }
        return emptyList()
    }

    private fun readLines(assetName: String): List<String> {
        return try {
            context.assets.open(assetName).bufferedReader().use { reader ->
                reader.readLines()
                    .mapNotNull { line ->
                        val trimmed = line.trim()
                        if (trimmed.isEmpty()) return@mapNotNull null
                        // Handle "index\tlabel" format (e.g. "1\tApple___Apple_scab")
                        val tabIdx = trimmed.indexOf('\t')
                        if (tabIdx >= 0) trimmed.substring(tabIdx + 1).trim() else trimmed
                    }
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    private fun readText(assetName: String): String {
        return try {
            context.assets.open(assetName).bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ""
        }
    }

    private fun parseJsonLabels(json: String): List<String> {
        return try {
            val array = JSONArray(json)
            val labels = ArrayList<String>(array.length())
            for (i in 0 until array.length()) {
                labels.add(array.optString(i))
            }
            labels
        } catch (ex: Exception) {
            emptyList()
        }
    }
}
