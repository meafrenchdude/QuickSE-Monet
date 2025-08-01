package com.meafrenchdude.quickse

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.topjohnwu.superuser.Shell
import com.meafrenchdude.quickse.util.isNotificationEnabled

class ToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Shell.isAppGrantedRoot() != true) {
            if (isNotificationEnabled(context)) {
                showNotification(context, "SELinux Toggle Failed", "Root access required")
            }
            return
        }

        val current = Shell.cmd("getenforce").exec().out.firstOrNull()?.trim() ?: "Enforcing"
        val new = if (current == "Enforcing") "0" else "1"
        Shell.cmd("setenforce $new").exec()

        val resultText = if (new == "0") "Permissive" else "Enforcing"

        if (isNotificationEnabled(context)) {
            showNotification(context, "SELinux Toggled", "Now: $resultText")
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "selinux_toggle_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SELinux Toggle Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_qse)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
