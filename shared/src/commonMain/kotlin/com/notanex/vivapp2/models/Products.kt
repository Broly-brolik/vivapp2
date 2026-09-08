package com.notanex.vivapp2.models

import kotlinx.serialization.Serializable

@Serializable
data class Products(
    val sapNumber: String,
    val name: String,
    val category: String,
    val minOrderQty: Int,
    val unit: String,
    val packaging: String,
    val netPrice: Double,
    val billingUnit: String,
    val weightKg: Double,
    val maxOrderQty: Int,
)
