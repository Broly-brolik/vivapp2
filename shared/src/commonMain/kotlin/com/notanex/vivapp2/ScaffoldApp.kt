package com.notanex.vivapp2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.notanex.vivapp2.viewmodels.InventoryViewModel

@Composable
fun ScaffoldApp() {
    val viewModel = InventoryViewModel()
    viewModel.loadInventory("products.csv")

    // Observe the StateFlow's current list of products
    val products = viewModel.products.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            for (product in products.value) {
                Text(
                    modifier = Modifier.padding(innerPadding),
                    text = "${product.sapNumber} · ${product.groupProducts} · ${product.packaging}",
                )
            }
        }
    }
}