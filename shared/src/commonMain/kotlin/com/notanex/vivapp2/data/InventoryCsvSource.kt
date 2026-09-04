package com.notanex.vivapp2.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vivapp2.shared.generated.resources.Res

class InventoryCsvSource {

    /**
     * Reads a CSV file from composeResources/files/ and breaks it into lines.
     *
     * @param fileName The name of the file inside composeResources/files/ (e.g., "products.csv")
     */
    suspend fun readCsvLines(fileName: String): List<String> = withContext(Dispatchers.Default) {
        // 1. Fetch raw byte array from the embedded resource bundle
        val byteArray = Res.readBytes("files/$fileName")

        // 2. Decode UTF-8 bytes to a String and split into individual lines
        byteArray.decodeToString()
            .lines()
            .filter { it.isNotBlank() } // Strip empty lines
    }
}