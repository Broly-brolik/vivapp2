package com.notanex.vivapp2.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import vivapp2.shared.generated.resources.Res

@Serializable
data class CatalogProduct(
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

suspend fun readJsonFile(fileName: String): List<CatalogProduct> {
    val byteArray = Res.readBytes("files/$fileName")
    return Json.decodeFromString<List<CatalogProduct>>(byteArray.decodeToString())
}