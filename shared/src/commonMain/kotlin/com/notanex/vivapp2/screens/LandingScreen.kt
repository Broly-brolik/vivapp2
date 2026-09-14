package com.notanex.vivapp2.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color definitions matching the design
private val OrangePrimary = Color(0xFFFF6E4B)
private val LightOrangeBg = Color(0xFFFFF0EC)
private val DarkText = Color(0xFF1E1E1E)
private val MutedText = Color(0xFF8E8E93)
private val ScreenBackground = Color(0xFFFFFFFF)

@Composable
fun LandingScreen(
    onOpenProducts: () -> Unit,
    onStartScan: () -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(ScreenBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Scan QR code",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = DarkText
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.size(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 6.dp.toPx()
                val cornerLength = 36.dp.toPx()
                val radius = 16.dp.toPx()

                drawPath(
                    path = Path().apply {
                        moveTo(0f, cornerLength)
                        lineTo(0f, radius)
                        quadraticTo(0f, 0f, radius, 0f)
                        lineTo(cornerLength, 0f)
                    },
                    color = OrangePrimary,
                    style = Stroke(width = strokeWidth)
                )

                drawPath(
                    path = Path().apply {
                        moveTo(size.width - cornerLength, 0f)
                        lineTo(size.width - radius, 0f)
                        quadraticTo(size.width, 0f, size.width, radius)
                        lineTo(size.width, cornerLength)
                    },
                    color = OrangePrimary,
                    style = Stroke(width = strokeWidth)
                )

                drawPath(
                    path = Path().apply {
                        moveTo(0f, size.height - cornerLength)
                        lineTo(0f, size.height - radius)
                        quadraticTo(0f, size.height, radius, size.height)
                        lineTo(cornerLength, size.height)
                    },
                    color = OrangePrimary,
                    style = Stroke(width = strokeWidth)
                )

                drawPath(
                    path = Path().apply {
                        moveTo(size.width - cornerLength, size.height)
                        lineTo(size.width - radius, size.height)
                        quadraticTo(size.width, size.height, size.width, size.height - radius)
                        lineTo(size.width, size.height - cornerLength)
                    },
                    color = OrangePrimary,
                    style = Stroke(width = strokeWidth)
                )
            }

            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                tint = DarkText
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = OrangePrimary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(0.6f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Switch to Barcode Mode */ }) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Barcode",
                    tint = MutedText
                )
            }
            IconButton(onClick = { /* Toggle Flash */ }) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = MutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Start scanning",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}