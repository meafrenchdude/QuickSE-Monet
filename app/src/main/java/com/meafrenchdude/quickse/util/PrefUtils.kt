package com.meafrenchdude.quickse.util

import android.content.Context

fun setAutoToggleEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences("quickse_prefs", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("auto_toggle_on_boot", enabled)
        .apply()
}

fun isAutoToggleEnabled(context: Context): Boolean {
    return context.getSharedPreferences("quickse_prefs", Context.MODE_PRIVATE)
        .getBoolean("auto_toggle_on_boot", false)
}

fun isNotificationEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences("quickse_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("notify_after_toggle", true)
}

fun setNotificationEnabled(context: Context, enabled: Boolean) {
    val prefs = context.getSharedPreferences("quickse_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("notify_after_toggle", enabled).apply()
}

