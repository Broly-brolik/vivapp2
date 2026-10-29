package com.notanex.vivapp2.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notanex.vivapp2.ui.theme.SwissRed
import org.jetbrains.compose.resources.stringResource
import qrscanner.CameraLens
import qrscanner.QrScanner
import vivapp2.shared.generated.resources.Res
import vivapp2.shared.generated.resources.action_flashlight
import vivapp2.shared.generated.resources.action_pick_from_list
import vivapp2.shared.generated.resources.action_scan_image
import vivapp2.shared.generated.resources.msg_camera_denied
import vivapp2.shared.generated.resources.msg_code_unknown
import vivapp2.shared.generated.resources.msg_scan_hint

@Composable
fun ScanScreen(
    onScanResult: (String) -> Unit,
    onPickFromList: () -> Unit,
    showUnknownCode: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    var flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }

    if (permissionDenied) {
        CameraDeniedView(onPickFromList = onPickFromList, contentPadding = contentPadding)
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        QrScanner(
            modifier = Modifier.fillMaxSize(),
            flashlightOn = flashlightOn,
            cameraLens = CameraLens.Back,
            openImagePicker = openImagePicker,
            onCompletion = onScanResult,
            imagePickerHandler = { openImagePicker = it },
            onFailure = { error -> println("DEBUG: Scan Error -> $error") },
            overlayBorderColor = SwissRed,
            permissionDeniedView = { LaunchedEffect(Unit) { permissionDenied = true } },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScanPill(text = stringResource(Res.string.msg_scan_hint))

            Spacer(Modifier.height(12.dp))

            AnimatedVisibility(visible = showUnknownCode, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.msg_code_unknown), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = { openImagePicker = true },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = stringResource(Res.string.action_scan_image))
                }
                FilledTonalIconToggleButton(
                    checked = flashlightOn,
                    onCheckedChange = { flashlightOn = it },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        if (flashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        contentDescription = stringResource(Res.string.action_flashlight)
                    )
                }
                FilledTonalIconButton(
                    onClick = onPickFromList,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(Res.string.action_pick_from_list))
                }
            }
        }
    }
}

@Composable
private fun ScanPill(text: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.6f),
        contentColor = Color.White,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CameraDeniedView(onPickFromList: () -> Unit, contentPadding: PaddingValues) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.NoPhotography,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(Res.string.msg_camera_denied),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onPickFromList) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.action_pick_from_list))
            }
        }
    }
}
