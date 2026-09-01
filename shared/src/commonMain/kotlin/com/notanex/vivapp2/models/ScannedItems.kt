package com.notanex.vivapp2.models

data class ScannedItem(
    val sapNumber: String,
    val groupProducts: String,
    val packaging: String,
    val quantity: Int = 1
)
