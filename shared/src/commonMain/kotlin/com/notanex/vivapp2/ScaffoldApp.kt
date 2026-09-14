package com.notanex.vivapp2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.notanex.vivapp2.logic.EmailAttachment
import com.notanex.vivapp2.logic.sendEmailWithAttachment
import com.notanex.vivapp2.screens.ItemConfirmationScreen
import com.notanex.vivapp2.screens.LandingScreen
import com.notanex.vivapp2.screens.ProductsScreen
import com.notanex.vivapp2.screens.ScanScreen
import com.notanex.vivapp2.screens.SummaryScreen
import com.notanex.vivapp2.viewmodels.InventoryViewModel
import com.notanex.vivapp2.viewmodels.ScannedItemsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp() {
    val viewModel = remember { InventoryViewModel() }

    val scannedItemsViewModel = remember { ScannedItemsViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadInventory("products.json")
    }

    val products by viewModel.products.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    val navController = rememberNavController()

    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val isLandingScreen = currentRoute?.endsWith("LandingRoute") == true
    val isProductsScreen = currentRoute?.endsWith("ProductsRoute") == true
    val isSummaryScreen = currentRoute?.endsWith("SummaryRoute") == true
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isProductsScreen -> "Products"
                            isSummaryScreen -> "Scanning History"
                            else -> "Vivapp"
                        }
                    )
                },
                navigationIcon = {
                    if (!isLandingScreen) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (isSummaryScreen || isLandingScreen) {
                        IconButton(onClick = { navController.navigate(ScanRoute) }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan another")
                        }
                    }

                    if (!isProductsScreen) {
                        IconButton(onClick = { navController.navigate(ProductsRoute) }) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Show products")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = LandingRoute,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<LandingRoute> {
                    LandingScreen(
                        onOpenProducts = { navController.navigate(ProductsRoute) },
                        onStartScan = { navController.navigate(ScanRoute) },
                        contentPadding = innerPadding,
                    )
                }
                composable<ProductsRoute> {
                    ProductsScreen(
                        products = products,
                        loadError = loadError,
                        contentPadding = innerPadding,
                    )
                }
                composable<ScanRoute> {
                    val scope = rememberCoroutineScope()
                    var isProcessing by remember { mutableStateOf(false) }
                    ScanScreen(
                        onScanResult = { rawCode ->
                            scope.launch(Dispatchers.Main) {
                                if (!isProcessing) {
                                    isProcessing = true

                                    val product = viewModel.findProductByQr(rawCode)
                                    if (product != null) {
                                        navController.navigate(ConfirmItemRoute(product.sapNumber))
                                    } else {
                                        println("DEBUG: No product for $rawCode")
                                        isProcessing = false
                                    }
                                }
                            }
                        },
                        onCancel = { navController.popBackStack() },
                    )
                }
                composable<ConfirmItemRoute> { backStackEntry ->
                    val route: ConfirmItemRoute = backStackEntry.toRoute()
                    val product = products.find { it.sapNumber == route.sapNumber }
                    var quantity by remember { mutableStateOf(1) }
                    if (product != null) {
                        ItemConfirmationScreen(
                            product = product,
                            quantity = quantity,
                            onIncrement = { quantity++ },
                            onDecrement = { if (quantity > 1) quantity-- },
                            onConfirm = {
                                scannedItemsViewModel.addScan(product, quantity)
                                navController.navigate(SummaryRoute) {
                                    popUpTo(LandingRoute) { inclusive = false }
                                }
                            },
                            onCancel = { navController.popBackStack() }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }

                }
                composable<SummaryRoute> {
                    val scannedItems by scannedItemsViewModel.scannedItems.collectAsState()

                    SummaryScreen(
                        scannedItems = scannedItems,
                        onIncrement = {sap -> scannedItemsViewModel.increment(sap) },
                        onDecrement = {sap -> scannedItemsViewModel.decrement(sap) },
                        onRemove = {sap -> scannedItemsViewModel.removeItem(sap) },
                        onClearAll = { scannedItemsViewModel.clearAll() },
                        onSendEmail = {
                            val csvContent = scannedItemsViewModel.buildCsvSummary()

                            sendEmailWithAttachment(
                                subject = "Scanned Inventory - ${scannedItems.size} items",
                                body = "Attached is the CSV summary of the current scanning session.",
                                attachment = EmailAttachment(
                                    fileName = "inventory_summary.csv",
                                    content = csvContent
                                )
                            )
                        },
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }