package com.maazm7d.quickse.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutDialog(
    versionName: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "About QuickSE",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    "A modern SELinux mode manager.",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                LabeledText(label = "App version", value = versionName)
                LabeledText(label = "Developer", value = "maazm7d")
                LabeledText(label = "Status", value = "Open Source")
                Spacer(Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(Modifier.height(14.dp))
                Text(
                    "License: GPL-3.0\n(c) 2025 QuickSE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun LabeledText(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
