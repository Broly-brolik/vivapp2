package com.notanex.vivapp2.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notanex.vivapp2.ui.theme.Ink
import com.notanex.vivapp2.ui.theme.MonoLabel
import com.notanex.vivapp2.ui.theme.Slate
import com.notanex.vivapp2.ui.theme.SwissRed

@Composable
fun LandingScreen(
    onOpenProducts: () -> Unit,
    onStartScan: () -> Unit,
    contentPadding: PaddingValues,
    itemsLoggedToday: Int = 0,
) {
    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {

        TopoBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(56.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SwissCrossMark()
                Spacer(Modifier.width(8.dp))
                Text(
                    "Vivapp",
                    style = MaterialTheme.typography.labelLarge,
                    color = Slate
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Scan an item to add it\nto today's manifest.",
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = SwissRed.copy(alpha = 0.45f),
                        spotColor = SwissRed.copy(alpha = 0.45f)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(SwissRed, Color(0xFFA31D14))))
                    .clickable(onClick = onStartScan),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text("Start scanning", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(itemsLoggedToday.toString(), style = MonoLabel.copy(fontSize = 20.sp), color = Ink)
                    Text("items logged today", style = MaterialTheme.typography.bodySmall, color = Slate)
                }
                TextButton(onClick = onOpenProducts) {
                    Text("View manifest", color = Slate)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SwissCrossMark(size: androidx.compose.ui.unit.Dp = 18.dp) {
    Box(modifier = Modifier.size(size)) {
        Box(
            Modifier.align(Alignment.Center).fillMaxWidth().height(size / 3.2f).background(SwissRed)
        )
        Box(
            Modifier.align(Alignment.Center).fillMaxHeight().width(size / 3.2f).background(SwissRed)
        )
    }
}

@Composable
private fun TopoBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val lineColor = Slate.copy(alpha = 0.10f)
        val rows = 7
        val rowHeight = size.height / rows
        repeat(rows) { i ->
            val baseY = rowHeight * i + rowHeight / 2f
            val amplitude = 22.dp.toPx() + (i % 3) * 12.dp.toPx()
            val step = size.width / 5f
            val path = Path().apply {
                moveTo(-40f, baseY)
                var x = -40f
                var up = (i % 2 == 0)
                while (x < size.width + 40f) {
                    val nextX = x + step
                    val controlY = if (up) baseY - amplitude else baseY + amplitude
                    cubicTo(x + step / 2f, controlY, x + step / 2f, controlY, nextX, baseY)
                    x = nextX
                    up = !up
                }
            }
            drawPath(path, color = lineColor, style = Stroke(width = 1.4.dp.toPx()))
        }

        val mark = Offset(size.width * 0.8f, size.height * 0.22f)
        drawCircle(color = SwissRed, radius = 4.dp.toPx(), center = mark)
        drawCircle(color = SwissRed.copy(alpha = 0.35f), radius = 15.dp.toPx(), center = mark, style = Stroke(width = 1.5.dp.toPx()))
    }
}