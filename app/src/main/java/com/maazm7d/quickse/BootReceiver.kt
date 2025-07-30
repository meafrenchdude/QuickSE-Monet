package com.maazm7d.quickse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.maazm7d.quickse.util.isAutoToggleEnabled
import com.maazm7d.quickse.util.isNotificationEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (
            intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_PACKAGE_REPLACED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON"
        ) return

        if (!isAutoToggleEnabled(context)) {
            showNotification(context, "QuickSE auto-toggle disabled.")
            return
        }


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "getenforce"))
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val currentStatus = reader.readLine()?.trim() ?: return@launch
                process.waitFor()

                val newMode = if (currentStatus.equals("Enforcing", true)) "0" else "1"
                val newStatus = if (newMode == "0") "Permissive" else "Enforcing"

                val toggleProcess = Runtime.getRuntime().exec("su")
                val output = toggleProcess.outputStream
                output.write("setenforce $newMode\n".toByteArray())
                output.write("exit\n".toByteArray())
                output.flush()
                toggleProcess.waitFor()

                val message = if (toggleProcess.exitValue() == 0) {
                    "Switched SELinux to $newStatus"
                } else {
                    "Failed to switch SELinux! Do you have root?"
                }

                showNotification(context, message)
            } catch (e: Exception) {
                showNotification(context, "Error switching SELinux.")
            }
        }
    }

    private fun showNotification(context: Context, message: String) {
        if (!isNotificationEnabled(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val channelId = "quickse_toggle_channel"
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_qse)
            .setContentTitle("QuickSE")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }
}
