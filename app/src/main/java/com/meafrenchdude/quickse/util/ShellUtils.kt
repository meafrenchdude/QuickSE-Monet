package com.meafrenchdude.quickse.util

import java.io.BufferedReader
import java.io.InputStreamReader

fun runCommand(command: String): String {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.readLine().trim()
    } catch (e: Exception) {
        ""
    }
}

fun getSelinuxStatus(): String {
    val result = runCommand("getenforce").trim()
    return when {
        result.equals("Enforcing", true) -> "Enforcing"
        result.equals("Permissive", true) -> "Permissive"
        else -> "Unknown"
    }
}
fun setSelinuxMode(mode: String): Boolean {
    runCommand("setenforce ${if (mode == "Enforcing") 1 else 0}")
    return getSelinuxStatus() == mode
}
