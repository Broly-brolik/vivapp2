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
    viewModel.loadInventory("products.json")

    val products = viewModel.products.collectAsState()
    val loadError = viewModel.loadError.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (loadError.value != null) {
                Text(
                    modifier = Modifier.padding(innerPadding),
                    text = "${loadError.value}",
                )
            } else {
                for (product in products.value) {
                    Text(
                        modifier = Modifier.padding(innerPadding),
                        text = "${product.sapNumber} · ${product.name} · ${product.packaging}",
                    )
                }
            }
        }
    }
}
