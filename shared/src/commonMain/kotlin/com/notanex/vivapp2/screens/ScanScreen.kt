package com.notanex.vivapp2.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun ScanScreen(
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit,
) {
    // 1. Add a guard state
    var isProcessing by remember { mutableStateOf(false) }

    val rememberedOnScan = remember(onScanResult) { onScanResult }
    val rememberedOnCancel = remember(onCancel) { onCancel }

    QrScanner(
        modifier = Modifier.fillMaxSize(),
        flashlightOn = false,
        cameraLens = CameraLens.Back,
        openImagePicker = false,
        onCompletion = { code ->
            if (!isProcessing) {
                isProcessing = true
                rememberedOnScan(code)
            }
        },
        imagePickerHandler = { },
        onFailure = { error ->
            println("DEBUG: Scan Error -> $error")
        },
        permissionDeniedView = {
            LaunchedEffect(Unit) { rememberedOnCancel() }
        },
    )
}