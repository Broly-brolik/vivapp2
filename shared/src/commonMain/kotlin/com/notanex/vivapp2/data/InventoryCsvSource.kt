package com.notanex.vivapp2.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vivapp2.shared.generated.resources.Res
import kotlin.js.JsFileName

class InventoryCsvSource {

    /**
     * Reads a CSV file from composeResources/files/ and breaks it into lines.
     *
     * @param fileName The name of the file inside composeResources/files/ (e.g., "products.csv")
     */
    suspend fun readCsvLines(fileName: String): List<String> = withContext(Dispatchers.Default) {
        val byteArray = Res.readBytes("files/$fileName")

        byteArray.decodeToString()
            .lines()
            .filter { it.isNotBlank() }
    }
}

