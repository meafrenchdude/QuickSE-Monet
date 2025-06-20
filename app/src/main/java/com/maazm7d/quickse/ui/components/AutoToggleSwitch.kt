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
import com.maazm7d.quickse.util.isAutoToggleEnabled
import com.maazm7d.quickse.util.setAutoToggleEnabled

@Composable
fun AutoToggleSwitch() {
    val context = LocalContext.current
    var autoToggle by remember { mutableStateOf(isAutoToggleEnabled(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text("Auto-toggle on boot")
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = autoToggle,
            onCheckedChange = {
                autoToggle = it
                setAutoToggleEnabled(context, it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

