package com.meafrenchdude.quickse

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.meafrenchdude.quickse.util.getSelinuxStatus
import com.meafrenchdude.quickse.util.setSelinuxMode

class ShortcutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentMode = getSelinuxStatus()
        val newMode = if (currentMode == "Enforcing") "Permissive" else "Enforcing"
        val success = setSelinuxMode(newMode)

        Toast.makeText(this,
            if (success) "Switched to $newMode"
            else "Failed to toggle SELinux mode",
            Toast.LENGTH_SHORT).show()

        finish() // Close activity immediately
    }
}

