package com.meafrenchdude.quickse

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class SelinuxApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "quickse_toggle_channel",                  
                "QuickSE Auto Toggle",                     
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when SELinux mode is toggled at boot"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
