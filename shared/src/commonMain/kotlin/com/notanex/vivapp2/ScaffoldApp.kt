package com.notanex.vivapp2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notanex.vivapp2.screens.LandingScreen
import com.notanex.vivapp2.screens.ProductsScreen
import com.notanex.vivapp2.screens.ScanScreen
import com.notanex.vivapp2.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp() {
    val viewModel = remember { InventoryViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadInventory("products.json")
    }

    val products by viewModel.products.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    val navController = rememberNavController()

    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val isProductsScreen = currentRoute?.endsWith("ProductsRoute") == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isProductsScreen) "Products" else "Vivapp") },
                navigationIcon = {
                    if (isProductsScreen) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (!isProductsScreen) {
                        IconButton(onClick = { navController.navigate(ProductsRoute) }) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Show products")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = LandingRoute
            ) {
                composable<LandingRoute> {
                    LandingScreen(
                        onOpenProducts = { navController.navigate(ProductsRoute) },
                        onStartScan = { navController.navigate(ScanRoute) },
                    )
                }
                composable<ProductsRoute> {
                    ProductsScreen(
                        products = products,
                        loadError = loadError
                    )
                }
                composable<ScanRoute> {
                    ScanScreen(
                        onScanResult = { code ->
                            val product = viewModel.findProduct(code)          // add this to InventoryViewModel
                            if (product != null) {
                                scannedItemsViewModel.addScan(product, 1)      // revive un-commented
                                navController.popBackStack()                   // back to Landing / summary later
                            } else {
                                // "Unknown code" — keep scanner open or show error; don't silently swallow
                            }
                        },
                        onCancel = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}