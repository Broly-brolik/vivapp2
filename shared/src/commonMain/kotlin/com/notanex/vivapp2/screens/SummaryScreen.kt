package com.notanex.vivapp2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notanex.vivapp2.models.ScannedItem

@Composable
fun SummaryScreen(
    scannedItems: List<ScannedItem>,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit,
    onSendEmail: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (scannedItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onSendEmail,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Send Email")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerScaffoldPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerScaffoldPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scanning History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onClearAll) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear all")
                    }
                }

                if (scannedItems.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No items scanned yet", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(scannedItems, key = { it.sapNumber }) { item ->
                            ScannedItemRowCard(
                                item = item,
                                onIncrement = { onIncrement(item.sapNumber) },
                                onDecrement = { onDecrement(item.sapNumber) },
                                onRemove = { onRemove(item.sapNumber) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScannedItemRowCard(
    item: ScannedItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.category, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = "SAP: ${item.sapNumber}", style = MaterialTheme.typography.bodySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement) { Text("-", fontWeight = FontWeight.Bold) }
                Text(text = item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onIncrement) { Text("+", fontWeight = FontWeight.Bold) }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(20.dp))
            }
        }
    }
}