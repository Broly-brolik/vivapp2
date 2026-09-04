package com.notanex.vivapp2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.notanex.vivapp2.models.Products
import com.notanex.vivapp2.models.ScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import org.dhatim.fastexcel.reader.ReadableWorkbook

class InventoryViewModel : ViewModel() {

    private val _fileReadError = MutableStateFlow<String?>(null)
    val fileReaderError: StateFlow<String?> = _fileReadError.asStateFlow()
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    private val _productsBySap = MutableStateFlow<Map<String, Products>>(emptyMap())
    val productsBySap: StateFlow<Map<String, Products>> = _productsBySap.asStateFlow()
    private val _lastScannedQrCode = MutableStateFlow<String?>(null)
    val lastScannedQrCode: StateFlow<String?> = _lastScannedQrCode.asStateFlow()
    private val _scannedItemDetail = MutableStateFlow<String?>(null)
    val scannedItemDetail: StateFlow<String?> = _scannedItemDetail.asStateFlow()
    private val _scanResultMessage = MutableStateFlow<String?>(null)
    val scanResultMessage: StateFlow<String?> = _scanResultMessage.asStateFlow()

    private val DESIRED_COLUMN_HEADERS = listOf(
        "Article Groupe de produit",
        "SAP N°",
        "Emballage collectif"
    )

    private val DEFAULT_ARTICLE_GROUP_INDEX = 0
    private val DEFAULT_SAP_NUMBER_INDEX = 8
    private val DEFAULT_PACKAGING_INDEX = 13

    private val _scanUiState = MutableStateFlow(ScanUiState())
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    fun clearError() {
        _scanUiState.value = _scanUiState.value.copy(errorMessage = null)
    }

    fun readInventoryFile(filePath: String) {
        if (filePath.isBlank()) {
            Log.w("InventoryViewModel", "File path is blank in readInventoryFile.")
            _fileReadError.value = "Error: File path is blank for reading."
            return
        }
        val file = File(filePath)
        if (!file.exists()) {
            Log.d("InventoryVM", "File not found: $filePath")
            _products.value = emptyList()
            _productsBySap.value = emptyMap()
            return
        }

        viewModelScope.launch {
            val resultProducts = withContext(Dispatchers.IO) {
                try {
                    val allRows: List<List<String>> = csvReader().readAll(file)
                    val processedData = mutableListOf<Products>()

                    var sapColumnIndex = DEFAULT_SAP_NUMBER_INDEX
                    var articleGroupIndex = DEFAULT_ARTICLE_GROUP_INDEX
                    var packagingIndex = DEFAULT_PACKAGING_INDEX

                    val headerRow = allRows.firstOrNull { row ->
                        DESIRED_COLUMN_HEADERS.all { headerToFind ->
                            row.any { cell -> cell.trim().equals(headerToFind, ignoreCase = true) }
                        }
                    }

                    if (headerRow != null) {
                        val tempSapIdx = headerRow.indexOfFirst { it.trim().equals(DESIRED_COLUMN_HEADERS[1], ignoreCase = true) }
                        val tempArticleIdx = headerRow.indexOfFirst { it.trim().equals(DESIRED_COLUMN_HEADERS[0], ignoreCase = true) }
                        val tempPackagingIdx = headerRow.indexOfFirst { it.trim().equals(DESIRED_COLUMN_HEADERS[2], ignoreCase = true) }

                        if (tempSapIdx != -1 && tempArticleIdx != -1 && tempPackagingIdx != -1) {
                            sapColumnIndex = tempSapIdx
                            articleGroupIndex = tempArticleIdx
                            packagingIndex = tempPackagingIdx
                            Log.i("InventoryVM", "Dynamically found headers at indices: ArticleGroup=$articleGroupIndex, SAP=$sapColumnIndex, Packaging=$packagingIndex")
                        } else {
                            Log.w("InventoryVM", "Header row found but couldn't locate all desired headers. Falling back to defaults.")
                        }
                    } else {
                        Log.w("InventoryVM", "Header row containing desired columns not found. Using default indices.")
                    }

                    val indicesToKeep = listOf(articleGroupIndex, sapColumnIndex, packagingIndex)
                    val maxRequiredIndex = indicesToKeep.maxOrNull() ?: -1

                    allRows.forEach { row ->
                        val trimmedRow = row.map { it.trim() }
                        if (trimmedRow.size > maxRequiredIndex && trimmedRow.size > sapColumnIndex && trimmedRow[sapColumnIndex].isNotBlank()) {
                            try {
                                val articleGroup = trimmedRow.getOrNull(articleGroupIndex) ?: ""
                                val sap = trimmedRow.getOrNull(sapColumnIndex) ?: ""
                                val packagingStr = trimmedRow.getOrNull(packagingIndex) ?: ""

                                if (sap.isBlank()) {
                                    return@forEach
                                }

                                val packaging = packagingStr

                                val product = Products(
                                    sapNumber = sap,
                                    groupProducts = articleGroup,
                                    packaging = packaging,
                                    adjustment = 0
                                )

                                processedData.add(product)
                                Log.d("InventoryVM ProcessedRow", "${product.groupProducts} | ${product.sapNumber} | ${product.packaging}")
                            } catch (e: Exception) {
                                Log.e("InventoryVM", "Skipping row due to exception: ${trimmedRow.joinToString(" | ")}", e)
                            }
                        }
                    }

                    processedData
                } catch (e: Exception) {
                    Log.e("InventoryVM", "Error reading CSV file: $filePath", e)
                    _fileReadError.value = "Failed to read/process inventory: ${e.localizedMessage ?: "Unknown error"}"
                    emptyList()
                }
            }

            _products.value = resultProducts
            _productsBySap.value = resultProducts.associateBy { it.sapNumber }
            Log.d("InventoryVM", "Finished processing CSV. ${resultProducts.size} products loaded.")
        }
    }

    // maybe one day adapt to read xlsx instead of csv
    private fun readXlsxFile(file: File): List<List<String>> {
        val allRows = mutableListOf<List<String>>()
        try {
            ReadableWorkbook(file).use { workbook ->
                workbook.sheets.forEach { sheet ->
                    sheet.openStream().use { rowStream ->
                        rowStream.forEach { row ->
                            val rowData = mutableListOf<String>()
                            for (i in 0 until row.cellCount) {
                                rowData.add(row.getCellText(i) ?: "")
                            }
                            allRows.add(rowData)
                        }
                    }
                }
            }
        }  catch (e: Exception) {
            Log.e("InventoryVM", "Error reading XLSX", e)
        }
        return  allRows
    }

    private fun findItemByQrCode(qrCode: String): Products? {
        val regex = Regex("MAT(\\d{8})")
        val matchResult = regex.find(qrCode)
        val sapNumber = matchResult?.groupValues?.get(1)
        Log.e("InventoryVM", "SAP: $sapNumber")
        return _productsBySap.value[sapNumber]
    }

    fun onQrCodeScanned(qrData: String?) {
        if (qrData == null) {
            _scanUiState.value = _scanUiState.value.copy(
                lastQrRaw = null,
                pendingSap = null,
                pendingName = null,
                pendingPackaging = null,
                pendingQuantity = 1,
                errorMessage = "Scanning cancelled or failed."
            )
            return
        }

        _scanUiState.value = _scanUiState.value.copy(lastQrRaw = qrData, errorMessage = null)

        viewModelScope.launch {
            val foundItem = findItemByQrCode(qrData)
            if (foundItem != null) {
                _scanUiState.value = _scanUiState.value.copy(
                    pendingSap = foundItem.sapNumber,
                    pendingName = foundItem.groupProducts,
                    pendingPackaging = foundItem.packaging,
                    pendingQuantity = 1,
                    errorMessage = null
                )
            } else {
                _scanUiState.value = _scanUiState.value.copy(
                    pendingSap = null,
                    pendingName = null,
                    pendingPackaging = null,
                    pendingQuantity = 1,
                    errorMessage = "Item not found for QR: $qrData"
                )
            }
        }
    }

    fun pendingIncrement() {
        _scanUiState.value = _scanUiState.value.copy(
            pendingQuantity = _scanUiState.value.pendingQuantity + 1
        )
    }

    fun pendingDecrement() {
        val current = _scanUiState.value.pendingQuantity
        if (current > 1) {
            _scanUiState.value = _scanUiState.value.copy(pendingQuantity = current - 1)
        }
    }

    fun clearPending() {
        _scanUiState.value = _scanUiState.value.copy(
            pendingSap = null,
            pendingName = null,
            pendingPackaging = null,
            pendingQuantity = 1
        )
    }


    fun clearLastScanMessage() {
        _scanResultMessage.value = null
    }

    fun clearFileReadError() {
        _fileReadError.value = null
    }

}
