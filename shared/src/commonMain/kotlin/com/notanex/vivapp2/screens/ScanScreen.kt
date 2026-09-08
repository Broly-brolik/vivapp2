package com.notanex.vivapp2.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun ScanScreen(
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit,
) {
    QrScanner(
        modifier = Modifier,
        flashlightOn = false,
        cameraLens = CameraLens.Back,
        openImagePicker = false,
        onCompletion = { code -> onScanResult(code) },
        imagePickerHandler = { },
        onFailure = { error -> /* surface error via ScanUiState.errorMessage */ },
        permissionDeniedView = { onCancel() }, // iOS permission denied → treat as cancel
    )
}