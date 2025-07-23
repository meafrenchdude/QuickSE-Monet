package com.maazm7d.quickse.ui.screens

import android.content.*
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import java.io.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KernelInfoScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val kernelEntries = listOf(
        "Kernel Information" to readShell("uname -a"),
        "Kernel Version" to readFile("/proc/version"),
        "Boot Parameters" to readFile("/proc/cmdline"),
        "Loaded Kernel Modules" to readFile("/proc/modules"),
        "Kernel Symbols" to readFile("/proc/kallsyms"),
        "Kernel Configuration" to readShell("zcat /proc/config.gz"),
        "System Uptime" to readFile("/proc/uptime"),
        "System Load Average" to readFile("/proc/loadavg"),
        "CPU Information" to readFile("/proc/cpuinfo"),
        "Memory Information" to readMemInfoMB(),
        "SELinux Status" to readShell("getenforce"),
        "Kernel Logs (Tail)" to readShell("dmesg | tail -n 100")
    )


    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) kernelEntries
        else kernelEntries.filter {
            it.first.contains(searchQuery, true) || it.second.contains(searchQuery, true)
        }
    }

    CompositionLocalProvider(LocalSearchQuery provides searchQuery) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Kernel Inspector", fontFamily = FontFamily.Monospace) }
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        placeholder = { Text("Search Kernel Info...", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        singleLine = true
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                filtered.forEach { (title, content) ->
                    ExpandableKernelCard(title = title, fullText = content, context = context)
                }
            }
        }
    }
}

@Composable
fun ExpandableKernelCard(title: String, fullText: String, context: Context) {
    val isExpandable = fullText.lineCount() > 5
    val previewText = fullText.lineSequence().take(5).joinToString("\n")
    val search = LocalSearchQuery.current

    val matchesInHidden = isExpandable && search.isNotBlank()
            && !previewText.contains(search, true)
            && fullText.contains(search, true)

    var expanded by remember(fullText, search) { mutableStateOf(matchesInHidden) }

    val displayText = when {
        !isExpandable -> fullText
        expanded      -> fullText
        else          -> "$previewText\n..."
    }

    val highlightedText = remember(displayText, search) {
        highlightQueryText(displayText, search)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = isExpandable) {
                if (isExpandable) expanded = !expanded
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Monospace)
                Row {
                    IconButton(onClick = {
                        copyToClipboard(context, title, fullText)
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    IconButton(onClick = {
                        saveToFile(context, title, fullText)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                    if (isExpandable) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (expanded) "Collapse" else "Expand"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = highlightedText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun readMemInfoMB(): String = try {
    File("/proc/meminfo")
        .readLines()
        .joinToString("\n") { line ->
            val parts = line.trim().split(Regex("\\s+"))
            if (parts.size >= 2 && parts[1].toIntOrNull() != null)
                "${parts[0]} ${"%.1f".format(parts[1].toFloat() / 1024)} MB"
            else line
        }
} catch (e: Exception) {
    "⚠️ Couldn't read /proc/meminfo: ${e.message}"
}

fun highlightQueryText(fullText: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(fullText)

    val lcText = fullText.lowercase()
    val lcQuery = query.lowercase()
    val builder = buildAnnotatedString {
        var startIndex = 0
        while (startIndex < lcText.length) {
            val index = lcText.indexOf(lcQuery, startIndex)
            if (index == -1) {
                append(fullText.substring(startIndex))
                break
            }
            append(fullText.substring(startIndex, index))
            withStyle(
                style = SpanStyle(
                    background = Color.Yellow.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(fullText.substring(index, index + query.length))
            }
            startIndex = index + query.length
        }
    }
    return builder
}

val LocalSearchQuery = staticCompositionLocalOf { "" }

fun String.lineCount(): Int = count { it == '\n' } + 1

fun copyToClipboard(context: Context, label: String, text: String) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Copy failed", Toast.LENGTH_SHORT).show()
    }
}

fun saveToFile(context: Context, title: String, content: String) {
    try {
        val filename = title.toSafeFileName()
        val file = File(context.getExternalFilesDir(null), "$filename.txt")
        FileOutputStream(file).use { it.write(content.toByteArray()) }
        Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Saving failed", Toast.LENGTH_SHORT).show()
    }
}

fun String.toSafeFileName(): String =
    replace("/", "_").replace(" ", "_").replace(Regex("[^a-zA-Z0-9_]"), "")

fun readFile(path: String): String = try {
    File(path).readText().trim()
} catch (_: Exception) {
    "⚠️ Permission denied or unavailable: $path"
}

fun readShell(cmd: String): String = try {
    val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
    val output = process.inputStream.bufferedReader().readText().trim()
    output.ifEmpty { "No output" }
} catch (_: Exception) {
    "⚠️ Shell command failed: $cmd"
}