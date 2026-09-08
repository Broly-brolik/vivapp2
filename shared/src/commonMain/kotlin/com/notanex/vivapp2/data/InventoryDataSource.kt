package com.notanex.vivapp2.data

import com.notanex.vivapp2.models.Products
import kotlinx.serialization.json.Json
import vivapp2.shared.generated.resources.Res

class InventoryDataSource {

    /**
     * Reads a json file from composeResources/files/
     *
     * @param fileName The name of the file inside composeResources/files/ (e.g., "products.csv")
     */
    suspend fun readJsonFile(fileName: String): List<Products> {
        val byteArray = Res.readBytes("files/$fileName")
        return Json.decodeFromString<List<Products>>(byteArray.decodeToString())
    }
}

