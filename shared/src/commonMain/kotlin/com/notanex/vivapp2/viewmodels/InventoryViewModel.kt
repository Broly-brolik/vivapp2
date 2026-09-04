package com.notanex.vivapp2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notanex.vivapp2.data.InventoryCsvSource
import com.notanex.vivapp2.models.Products
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val csvSource: InventoryCsvSource = InventoryCsvSource(),
) : ViewModel() {

    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> = _products.asStateFlow()

    fun loadInventory(fileName: String) {
        viewModelScope.launch {
            try {
                val lines = csvSource.readCsvLines(fileName)
                val parsedProducts = parseCsvLines(lines)
                _products.value = parsedProducts
            } catch (e: Exception) {
                // TODO: Surface the error to the UI once error handling is added
            }
        }
    }

    private fun parseCsvLines(lines: List<String>): List<Products> {
        if (lines.isEmpty()) return emptyList()

        // Drop the header line if present
        val dataLines = if (lines.first().contains("sapNumber", ignoreCase = true)) {
            lines.drop(1)
        } else {
            lines
        }

        return dataLines.mapNotNull { line ->
            val columns = line.split(",").map { it.trim() }

            // Assuming CSV format: sapNumber, groupProducts, packaging, adjustment
            if (columns.size >= 4) {
                Products(
                    sapNumber = columns[0],
                    groupProducts = columns[1],
                    packaging = columns[2],
                    adjustment = columns[3].toInt(),
                )
            } else {
                null // Skip malformed rows
            }
        }
    }
}