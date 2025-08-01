package com.meafrenchdude.quickse.ui.components

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import com.meafrenchdude.quickse.util.cancelToggle
import com.meafrenchdude.quickse.util.scheduleOneTimeToggle
import com.meafrenchdude.quickse.R
import java.util.*

@Composable
fun ScheduledAutoToggleSwitch() {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(false) }
    var timeText by remember { mutableStateOf(context.getString(R.string.time_not_set)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.scheduled_toggle_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        isEnabled = checked
                        if (checked) {
                            val now = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    scheduleOneTimeToggle(context, hour, minute)
                                    timeText = String.format("%02d:%02d", hour, minute)
                                    Toast.makeText(
                                        context,
                                        context.getString(
                                            R.string.toggle_scheduled_format,
                                            timeText
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                false
                            ).show()
                        } else {
                            cancelToggle(context)
                            timeText = context.getString(R.string.time_not_set)
                            Toast.makeText(
                                context,
                                context.getString(R.string.toggle_cancelled),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.semantics {},
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            if (isEnabled) {
                Text(
                    text = stringResource(R.string.scheduled_time_prefix, timeText),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 12.dp, top = 4.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

