package com.notanex.vivapp2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notanex.vivapp2.data.InventoryDataSource
import com.notanex.vivapp2.models.Products
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val dataSource: InventoryDataSource = InventoryDataSource(),
) : ViewModel() {
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> = _products.asStateFlow()
    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()
    fun loadInventory(fileName: String) {
        viewModelScope.launch {
            try {
                _products.value = dataSource.readJsonFile(fileName)
                _loadError.value = null
            } catch (e: Exception) {
                _products.value = emptyList()
                _loadError.value = e.message ?: "Failed to load inventory"
            }
        }
    }

    fun findProduct(sapNumber: String): Products? = _products.value.firstOrNull() { it.sapNumber == sapNumber }

    fun onScanResult(rawCode: String) {
        println("DEBUG: QR Code Raw Data -> '$rawCode'")
        val sap = parseSapFromRaw(rawCode)
        val product = products.value.firstOrNull() { it.sapNumber == sap}
    }

    private fun parseSapFromRaw(raw: String): String {
        return raw.trim()
    }


    private fun findItemByQrCode(qrCode: String): Products? {
        val regex = Regex("MAT(\\d{8})")
        val matchResult = regex.find(qrCode)
        val sapNumber = matchResult?.groupValues?.get(1)
        return _products.value[sapNumber?.toInt() ?: -1]
    }

//    private fun parseCsvLines(lines: List<String>): List<Products> {
//        if (lines.isEmpty()) return emptyList()
//
//        val dataLines = if (lines.first().contains("sapNumber", ignoreCase = true)) {
//            lines.drop(1)
//        } else {
//            lines
//        }
//
//        return dataLines.mapNotNull { line ->
//            val columns = line.split(",").map { it.trim() }
//
//            if (columns.size >= 4) {
//                Products(
//                    sapNumber = columns[0],
//                    groupProducts = columns[1],
//                    packaging = columns[2],
//                    adjustment = columns[3].toInt(),
//                )
//            } else {
//                null // Skip malformed rows
//            }
//        }
//    }
}