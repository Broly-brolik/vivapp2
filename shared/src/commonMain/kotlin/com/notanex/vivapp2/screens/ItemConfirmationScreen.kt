package com.notanex.vivapp2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notanex.vivapp2.models.Products
import org.jetbrains.compose.resources.stringResource
import vivapp2.shared.generated.resources.Res
import vivapp2.shared.generated.resources.action_add_to_list
import vivapp2.shared.generated.resources.action_cancel
import vivapp2.shared.generated.resources.label_packaging
import vivapp2.shared.generated.resources.label_unit

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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "SAP: ${product.sapNumber}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${stringResource(Res.string.label_unit)}: ${product.unit}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${stringResource(Res.string.label_packaging)}: ${product.packaging}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = product.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement) { Icon(Icons.Default.Remove, "Less") }
                Text(text = quantity.toString(), style = MaterialTheme.typography.displayMedium)
                IconButton(onClick = onIncrement) { Icon(Icons.Default.Add, "More") }
            }

            Button(onClick = onConfirm) { Text(stringResource(Res.string.action_add_to_list)) }
            TextButton(onClick = onCancel) { Text(stringResource(Res.string.action_cancel)) }
        }
    }
}