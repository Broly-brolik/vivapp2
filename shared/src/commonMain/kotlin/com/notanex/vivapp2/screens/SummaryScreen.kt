package com.notanex.vivapp2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notanex.vivapp2.models.ScannedItem
import com.notanex.vivapp2.ui.theme.MonoLabel
import org.jetbrains.compose.resources.stringResource
import vivapp2.shared.generated.resources.Res
import vivapp2.shared.generated.resources.action_cancel
import vivapp2.shared.generated.resources.action_clear_all
import vivapp2.shared.generated.resources.action_pick_from_list
import vivapp2.shared.generated.resources.action_start_scanning
import vivapp2.shared.generated.resources.cd_decrease
import vivapp2.shared.generated.resources.cd_increase
import vivapp2.shared.generated.resources.cd_remove
import vivapp2.shared.generated.resources.dialog_clear_text
import vivapp2.shared.generated.resources.dialog_clear_title
import vivapp2.shared.generated.resources.msg_no_items
import vivapp2.shared.generated.resources.msg_no_items_hint
import vivapp2.shared.generated.resources.msg_session_total

@Composable
fun SummaryScreen(
    scannedItems: List<ScannedItem>,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onRemove: (ScannedItem) -> Unit,
    onClearAll: () -> Unit,
    onStartScan: () -> Unit,
    onPickFromList: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var showClearDialog by remember { mutableStateOf(false) }
    val totalQuantity = scannedItems.sumOf { it.quantity }

    Surface(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        if (scannedItems.isEmpty()) {
            EmptySummary(onStartScan = onStartScan, onPickFromList = onPickFromList)
            return@Surface
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.msg_session_total, totalQuantity, scannedItems.size),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { showClearDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(Res.string.action_clear_all))
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                // Leave room for the extended FAB so the last row stays reachable.
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(scannedItems, key = { it.sapNumber }) { item ->
                    ScannedItemRowCard(
                        item = item,
                        onIncrement = { onIncrement(item.sapNumber) },
                        onDecrement = { onDecrement(item.sapNumber) },
                        onRemove = { onRemove(item) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null) },
            title = { Text(stringResource(Res.string.dialog_clear_title)) },
            text = { Text(stringResource(Res.string.dialog_clear_text, totalQuantity)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearAll()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(Res.string.action_clear_all)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun EmptySummary(onStartScan: () -> Unit, onPickFromList: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(stringResource(Res.string.msg_no_items), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(Res.string.msg_no_items_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStartScan) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.action_start_scanning))
        }
        TextButton(onClick = onPickFromList) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.action_pick_from_list))
        }
    }
}

@Composable
private fun ScannedItemRowCard(
    item: ScannedItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(start = 16.dp, top = 14.dp, end = 8.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(item.sapNumber, style = MonoLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.packaging,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = stringResource(Res.string.cd_remove),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = onDecrement, enabled = item.quantity > 1) {
                    Icon(Icons.Default.Remove, contentDescription = stringResource(Res.string.cd_decrease))
                }
                Text(
                    text = item.quantity.toString(),
                    style = MonoLabel.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 48.dp)
                )
                FilledTonalIconButton(onClick = onIncrement) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.cd_increase))
                }
            }
        }
    }
}
