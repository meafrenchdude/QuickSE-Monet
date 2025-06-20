package com.maazm7d.quickse.util

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

