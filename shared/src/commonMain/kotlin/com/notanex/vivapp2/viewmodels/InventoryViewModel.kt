package com.notanex.vivapp2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notanex.vivapp2.data.InventoryDataSource
import com.notanex.vivapp2.models.AppLanguage
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

    private val _currentLanguage = MutableStateFlow(AppLanguage.FRENCH)
    val currentLanguage = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        loadInventory(language.fileName) // Reload the correct JSON
    }
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

    private fun extractSapFromQr(qrCode: String): String? {
        val regex = Regex("MAT(\\d{8})")
        return regex.find(qrCode)?.groupValues?.get(1)?.let {
            it.take(4) + "." + it.drop(4)
        }
    }

    fun findProductByQr(qrCode: String): Products? {
        val sap = extractSapFromQr(qrCode) ?: return null
        return _products.value.find { it.sapNumber == sap }
    }
}