package com.maazm7d.quickse.ui.components

import android.widget.Toast
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.SwitchDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import com.maazm7d.quickse.util.isAutoToggleEnabled
import com.maazm7d.quickse.util.setAutoToggleEnabled
import androidx.compose.material3.MaterialTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush

@Composable
fun AutoToggleSwitch() {
    val context = LocalContext.current
    var autoToggle by remember { mutableStateOf(isAutoToggleEnabled(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Notification permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Auto-toggle on boot",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
   Switch(
       checked = autoToggle,
       onCheckedChange = {
           autoToggle = it
           setAutoToggleEnabled(context, it)

           val message = if (it) "Auto-toggle enabled" else "Auto-toggle disabled"
           Toast.makeText(
               context,
               message,
               Toast.LENGTH_SHORT
           ).show()
       },
       colors = SwitchDefaults.colors(
           checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
           checkedTrackColor = MaterialTheme.colorScheme.primary,
           uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
           uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
              )
           )
        }
    }
}

