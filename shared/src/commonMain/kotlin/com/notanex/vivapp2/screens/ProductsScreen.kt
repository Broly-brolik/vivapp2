package com.notanex.vivapp2.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notanex.vivapp2.models.Products

@Composable
fun ProductsScreen(products: List<Products>, loadError: String?) {
    if (loadError != null) {
        Text(
            text = loadError,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(products) { product ->
                Text(
                    text = "${product.sapNumber} | ${product.name} | ${product.packaging}",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}