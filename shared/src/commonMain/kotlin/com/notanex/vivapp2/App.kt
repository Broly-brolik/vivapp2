package com.notanex.vivapp2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notanex.vivapp2.ui.theme.VivappTheme
import com.notanex.vivapp2.viewmodels.InventoryViewModel

@Composable
fun App(
    inventoryViewModel: InventoryViewModel = viewModel { InventoryViewModel() },
    systemLanguageCode: String = "en"
) {
    LaunchedEffect(Unit) {
        inventoryViewModel.initDefaultInventory(systemLanguageCode)
    }
    VivappTheme {
        ScaffoldApp(inventoryViewModel)
    }
}

