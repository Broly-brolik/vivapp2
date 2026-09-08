package com.notanex.vivapp2.screens

import androidx.compose.runtime.Composable
// import the QrScanner composable from QRKit (package per README — verify exact import path)
import network.chaintech.qrkit.QrScanner  // ← confirm real package from the lib
import qrscanner.QrScanner

@Composable
fun ScanScreen(
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit,
) {
    QrScanner(
        // flashlightOn = false, cameraLens = ..., openImagePicker = false,
        onCompletion = { code -> onScanResult(code) },
        onFailure = { error -> /* surface via ScanUiState errorMessage */ },
        permissionDeniedView = { /* "camera access denied" — user returns */ },
        // overlay* / zoom* / customOverlay* — theme to your brand later
    )
}