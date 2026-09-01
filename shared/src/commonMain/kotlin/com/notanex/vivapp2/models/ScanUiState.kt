package com.notanex.vivapp2.models
data class ScanUiState(
    val lastQrRaw: String? = null,
    val pendingSap: String? = null,
    val pendingName: String? = null,
    val pendingPackaging: String? = null,
    val pendingQuantity: Int = 1,
    val errorMessage: String? = null
)
