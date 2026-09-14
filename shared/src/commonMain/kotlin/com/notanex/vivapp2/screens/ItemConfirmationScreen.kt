package com.notanex.vivapp2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.notanex.vivapp2.models.Products

@Composable
fun ItemConfirmationScreen(
    product: Products,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Better for a confirm screen
        ) {
            Text(text = "SAP: ${product.sapNumber}")

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement) { Icon(Icons.Default.Remove, "Less") }
                Text(text = quantity.toString(), style = MaterialTheme.typography.displayMedium)
                IconButton(onClick = onIncrement) { Icon(Icons.Default.Add, "More") }
            }

            Button(onClick = onConfirm) { Text("Add to List") }
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}