package com.notanex.vivapp2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.notanex.vivapp2.logic.EmailAttachment
import com.notanex.vivapp2.logic.sendEmailWithAttachment
import com.notanex.vivapp2.models.AppLanguage
import com.notanex.vivapp2.screens.ItemConfirmationScreen
import com.notanex.vivapp2.screens.LandingScreen
import com.notanex.vivapp2.screens.ProductsScreen
import com.notanex.vivapp2.screens.ScanScreen
import com.notanex.vivapp2.screens.SummaryScreen
import com.notanex.vivapp2.viewmodels.InventoryViewModel
import com.notanex.vivapp2.viewmodels.ScannedItemsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import vivapp2.shared.generated.resources.Res
import vivapp2.shared.generated.resources.action_send_csv
import vivapp2.shared.generated.resources.action_start_scanning
import vivapp2.shared.generated.resources.action_undo
import vivapp2.shared.generated.resources.cd_back
import vivapp2.shared.generated.resources.cd_change_language
import vivapp2.shared.generated.resources.email_body
import vivapp2.shared.generated.resources.email_subject
import vivapp2.shared.generated.resources.history
import vivapp2.shared.generated.resources.msg_added
import vivapp2.shared.generated.resources.msg_removed
import vivapp2.shared.generated.resources.product
import vivapp2.shared.generated.resources.title_confirm
import vivapp2.shared.generated.resources.title_scan

/** How long the "code not recognised" banner stays up; repeats of the same code are ignored meanwhile. */
private const val UNKNOWN_CODE_BANNER_MS = 2500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp(viewModel: InventoryViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var showLanguageMenu by remember { mutableStateOf(false) }

    val scannedItemsViewModel = remember { ScannedItemsViewModel() }
    val scannedItems by scannedItemsViewModel.scannedItems.collectAsState()

    val products by viewModel.products.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val isLandingScreen = currentRoute?.endsWith("LandingRoute") == true
    val isProductsScreen = currentRoute?.endsWith("ProductsRoute") == true
    val isSummaryScreen = currentRoute?.endsWith("SummaryRoute") == true
    val isScanScreen = currentRoute?.endsWith("ScanRoute") == true
    val isConfirmScreen = currentRoute?.contains("ConfirmItemRoute") == true

    val emailSubject = stringResource(Res.string.email_subject, scannedItems.size)
    val emailBody = stringResource(Res.string.email_body)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isProductsScreen -> stringResource(Res.string.product)
                            isSummaryScreen -> stringResource(Res.string.history)
                            isScanScreen -> stringResource(Res.string.title_scan)
                            isConfirmScreen -> stringResource(Res.string.title_confirm)
                            else -> ""
                        }
                    )
                },
                navigationIcon = {
                    if (!isLandingScreen) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.cd_back))
                        }
                    }
                },
                actions = {
                    if (isSummaryScreen && scannedItems.isNotEmpty()) {
                        IconButton(onClick = { navController.navigate(ScanRoute) }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(Res.string.action_start_scanning))
                        }
                    }
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(Icons.Default.Language, contentDescription = stringResource(Res.string.cd_change_language))
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.label) },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        showLanguageMenu = false
                                    },
                                    leadingIcon = {
                                        if (currentLanguage == lang) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isSummaryScreen && scannedItems.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        sendEmailWithAttachment(
                            subject = emailSubject,
                            body = emailBody,
                            attachment = EmailAttachment(
                                fileName = "inventory_summary.csv",
                                content = scannedItemsViewModel.buildCsvSummary()
                            )
                        )
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null) },
                    text = { Text(stringResource(Res.string.action_send_csv)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
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
                        onOpenSummary = { navController.navigate(SummaryRoute) },
                        contentPadding = innerPadding,
                        itemsLoggedToday = scannedItems.sumOf { it.quantity }
                    )
                }
                composable<ProductsRoute> {
                    ProductsScreen(
                        products = products,
                        loadError = loadError,
                        onProductClick = { navController.navigate(ConfirmItemRoute(it.sapNumber)) },
                        quantityInList = { sap -> scannedItems.find { it.sapNumber == sap }?.quantity ?: 0 },
                        contentPadding = innerPadding,
                    )
                }
                composable<ScanRoute> {
                    var isProcessing by remember { mutableStateOf(false) }
                    var lastUnknownCode by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(lastUnknownCode) {
                        if (lastUnknownCode != null) {
                            delay(UNKNOWN_CODE_BANNER_MS)
                            lastUnknownCode = null
                        }
                    }

                    ScanScreen(
                        onScanResult = { rawCode ->
                            scope.launch(Dispatchers.Main) {
                                if (isProcessing || rawCode == lastUnknownCode) return@launch
                                isProcessing = true

                                val product = viewModel.findProductByQr(rawCode)
                                if (product != null) {
                                    navController.navigate(ConfirmItemRoute(product.sapNumber))
                                } else {
                                    lastUnknownCode = rawCode
                                    isProcessing = false
                                }
                            }
                        },
                        onPickFromList = {
                            navController.navigate(ProductsRoute) {
                                popUpTo(ScanRoute) { inclusive = true }
                            }
                        },
                        showUnknownCode = lastUnknownCode != null,
                        contentPadding = innerPadding,
                    )
                }
                composable<ConfirmItemRoute> { backStackEntry ->
                    val route: ConfirmItemRoute = backStackEntry.toRoute()
                    val product = products.find { it.sapNumber == route.sapNumber }
                    var quantity by rememberSaveable { mutableStateOf(1) }
                    if (product != null) {
                        fun addAndAnnounce() {
                            scannedItemsViewModel.addScan(product, quantity)
                            val added = quantity
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    getString(Res.string.msg_added, added, product.name),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                        ItemConfirmationScreen(
                            product = product,
                            quantity = quantity,
                            alreadyInList = scannedItems.find { it.sapNumber == product.sapNumber }?.quantity ?: 0,
                            onIncrement = { quantity++ },
                            onDecrement = { if (quantity > 1) quantity-- },
                            onConfirm = {
                                addAndAnnounce()
                                navController.navigate(SummaryRoute) {
                                    popUpTo(LandingRoute) { inclusive = false }
                                }
                            },
                            onConfirmAndScan = {
                                addAndAnnounce()
                                navController.navigate(ScanRoute) {
                                    popUpTo(LandingRoute) { inclusive = false }
                                }
                            },
                            onCancel = { navController.popBackStack() },
                            contentPadding = innerPadding
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }

                }
                composable<SummaryRoute> {
                    SummaryScreen(
                        scannedItems = scannedItems,
                        onIncrement = { sap -> scannedItemsViewModel.increment(sap) },
                        onDecrement = { sap -> scannedItemsViewModel.decrement(sap) },
                        onRemove = { item ->
                            val index = scannedItems.indexOf(item)
                            scannedItemsViewModel.removeItem(item.sapNumber)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                val result = snackbarHostState.showSnackbar(
                                    message = getString(Res.string.msg_removed, item.name),
                                    actionLabel = getString(Res.string.action_undo),
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    scannedItemsViewModel.restoreItem(item, index)
                                }
                            }
                        },
                        onClearAll = { scannedItemsViewModel.clearAll() },
                        onStartScan = { navController.navigate(ScanRoute) },
                        onPickFromList = { navController.navigate(ProductsRoute) },
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
