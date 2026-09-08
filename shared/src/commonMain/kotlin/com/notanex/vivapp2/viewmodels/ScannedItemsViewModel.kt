package com.notanex.vivapp2.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.notanex.vivapp2.models.Products
//import com.notanex.vivapp2.models.ScannedItem
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//class ScannedItemsViewModel : ViewModel() {
//
//    private val _scannedItems = MutableStateFlow<List<ScannedItem>>(emptyList())
//    val scannedItems: StateFlow<List<ScannedItem>> = _scannedItems.asStateFlow()
//
//    /**
//     * Add a scan for the given product. If same SAP already exists, increment quantity.
//     */
//    fun addScan(product: Products, quantity: Int) {
//        viewModelScope.launch {
//            val current = _scannedItems.value.toMutableList()
//            val idx = current.indexOfFirst { it.sapNumber == product.sapNumber }
//
//            if (idx != -1) {
//                val old = current[idx]
//                current[idx] = old.copy(quantity = old.quantity + quantity)
//            } else {
//                current.add(
//                    ScannedItem(
//                        sapNumber = product.sapNumber,
//                        groupProducts = product.groupProducts,
//                        packaging = product.packaging,
//                        quantity = quantity
//                    )
//                )
//            }
//            _scannedItems.value = current
//        }
//    }
//
//
//    /**
//     * Increment by one (explicit).
//     */
//    fun increment(sapNumber: String) {
//        viewModelScope.launch {
//            val current = _scannedItems.value.map {
//                if (it.sapNumber == sapNumber) it.copy(quantity = it.quantity + 1) else it
//            }
//            _scannedItems.value = current
//        }
//    }
//
//    /**
//     * Decrement one; remove item if quantity reaches 0.
//     */
//    fun decrement(sapNumber: String) {
//        viewModelScope.launch {
//            val current = _scannedItems.value.toMutableList()
//            val idx = current.indexOfFirst { it.sapNumber == sapNumber }
//            if (idx != -1) {
//                val old = current[idx]
//                if (old.quantity > 1) {
//                    current[idx] = old.copy(quantity = old.quantity - 1)
//                } else {
//                    current.removeAt(idx)
//                }
//                _scannedItems.value = current
//            }
//        }
//    }
//
//    fun removeItem(sapNumber: String) {
//        viewModelScope.launch {
//            _scannedItems.value = _scannedItems.value.filterNot { it.sapNumber == sapNumber }
//        }
//    }
//
//    fun clearAll() {
//        viewModelScope.launch {
//            _scannedItems.value = emptyList()
//        }
//    }
//
//    fun buildCsvSummary(): String {
//        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//        val header = listOf("GeneratedAt", "SAP Number", "GroupProducts", "Quantity").joinToString(",")
//        val rows = _scannedItems.value.joinToString("\n") { item ->
//            // escape any commas if needed (simple approach: wrap fields with quotes)
//            listOf(now, quote(item.sapNumber), quote(item.groupProducts), item.quantity.toString())
//                .joinToString(",")
//        }
//        return "$header\n$rows"
//    }
//
//    private fun quote(s: String) = "\"${s.replace("\"", "\"\"")}\""
//}
