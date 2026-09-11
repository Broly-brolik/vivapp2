package com.notanex.vivapp2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notanex.vivapp2.models.Products
import com.notanex.vivapp2.models.ScannedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ScannedItemsViewModel : ViewModel() {

    private val _scannedItems = MutableStateFlow<List<ScannedItem>>(emptyList())
    val scannedItems: StateFlow<List<ScannedItem>> = _scannedItems.asStateFlow()

    fun addScan(product: Products, quantity: Int) {
        viewModelScope.launch {
            val current = _scannedItems.value.toMutableList()
            val idx = current.indexOfFirst { it.sapNumber == product.sapNumber }

            if (idx != -1) {
                val old = current[idx]
                current[idx] = old.copy(quantity = old.quantity + quantity)
            } else {
                current.add(
                    ScannedItem(
                        sapNumber = product.sapNumber,
                        category = product.category,
                        packaging = product.packaging,
                        quantity = quantity
                    )
                )
            }
            _scannedItems.value = current
        }
    }

    fun increment(sapNumber: String) {
        viewModelScope.launch {
            _scannedItems.value = _scannedItems.value.map {
                if (it.sapNumber == sapNumber) it.copy(quantity = it.quantity + 1) else it
            }
        }
    }

    fun decrement(sapNumber: String) {
        viewModelScope.launch {
            val current = _scannedItems.value.toMutableList()
            val idx = current.indexOfFirst { it.sapNumber == sapNumber }
            if (idx != -1) {
                val old = current[idx]
                if (old.quantity > 1) {
                    current[idx] = old.copy(quantity = old.quantity - 1)
                } else {
                    current.removeAt(idx)
                }
                _scannedItems.value = current
            }
        }
    }

    fun removeItem(sapNumber: String) {
        viewModelScope.launch {
            _scannedItems.value = _scannedItems.value.filterNot { it.sapNumber == sapNumber }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            _scannedItems.value = emptyList()
        }
    }

    fun buildCsvSummary(): String {
        val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val header = listOf("GeneratedAt", "SAP Number", "GroupProducts", "Quantity").joinToString(",")
        val rows = _scannedItems.value.joinToString("\n") { item ->
            listOf(now.toString(), quote(item.sapNumber), quote(item.category), item.quantity.toString())
                .joinToString(",")
        }
        return "$header\n$rows"
    }

    private fun quote(s: String) = "\"${s.replace("\"", "\"\"")}\""
}