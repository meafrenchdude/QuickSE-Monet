package com.maazm7d.quickse.ui.screens

import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream

data class KernelSection(val title: String, val content: String)
data class CpuCoreInfo(val id: String, val governor: String, val minFreq: String, val maxFreq: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KernelInfoScreen() {
    val info = remember { getKernelInfo() }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Kernel", "Device", "CPU", "Cluster")
    val context = LocalContext.current

    val sections = remember(info) {
        val pattern = Regex("===== (.*?) =====")
        val matches = pattern.findAll(info)

        val splitPoints = matches.map { it.range.first }.toList() + info.length
        val result = mutableListOf<KernelSection>()

        for (i in 0 until matches.count()) {
            val title = matches.elementAt(i).groupValues[1]
            val start = matches.elementAt(i).range.last + 1
            val end = splitPoints[i + 1]
            val content = info.substring(start, end).trim()
            result.add(KernelSection(title.uppercase(), content))
        }

        result
    }

    val tabSections = remember(sections) {
        mapOf(
            "Kernel" to sections.find { it.title.contains("KERNEL") },
            "Device" to sections.find { it.title.contains("DEVICE") },
            "Cluster" to sections.find { it.title.contains("CLUSTER") }
        )
    }

    val selectedSection = tabSections[tabs[selectedTabIndex]]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device & Kernel Info", fontFamily = FontFamily.Monospace) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                    IconButton(onClick = {
                        selectedSection?.let {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Kernel Info", it.content)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }

                    IconButton(onClick = {
                        selectedSection?.let {
                            val fileName = "kernel_info.txt"
                            val file = File(context.getExternalFilesDir(null), fileName)
                            try {
                                FileOutputStream(file).use { stream: FileOutputStream ->
                                    stream.write(it.content.toByteArray())
                                }
                                Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tab,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            if (tabs[selectedTabIndex] == "CPU") {
                CpuCoreTable(cores = getCpuCoreInfo())
            } else {
                selectedSection?.let {
                    ExpandableCard(title = it.title, content = it.content)
                } ?: Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data found for this tab.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CpuCoreTable(cores: List<CpuCoreInfo>) {
    Column(Modifier.padding(16.dp)) {
        Text(
            text = "CPU Cores (${cores.size})",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("CPU", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("Governor", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("Min", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("Max", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        HorizontalDivider(Modifier.padding(vertical = 4.dp))

        cores.forEach { core ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(core.id, fontFamily = FontFamily.Monospace)
                Text(core.governor, fontFamily = FontFamily.Monospace)
                Text(core.minFreq, fontFamily = FontFamily.Monospace)
                Text(core.maxFreq, fontFamily = FontFamily.Monospace)
            }
            HorizontalDivider()
        }
    }
}

@Composable
fun ExpandableCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            if (expanded) {
                Text(
                    text = content,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            } else {
                Text(
                    text = "Tap to expand...",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun getKernelInfo(): String {
    val result = StringBuilder()

    fun read(path: String): String {
        return try {
            File(path).readText().trim()
        } catch (_: Exception) {
            "N/A"
        }
    }

    result.appendLine("===== Kernel =====")
    result.appendLine("Kernel Version : ${System.getProperty("os.version")}")
    result.appendLine("Build Version  : ${read("/proc/version")}")
    result.appendLine()

    result.appendLine("===== Device Info =====")
    result.appendLine("Device Name    : ${Build.DEVICE}")
    result.appendLine("Board          : ${Build.BOARD}")
    result.appendLine("Model          : ${Build.MODEL}")
    result.appendLine("Brand          : ${Build.BRAND}")
    result.appendLine("API Level      : ${Build.VERSION.SDK_INT}")
    result.appendLine("CPU ABI        : ${Build.SUPPORTED_ABIS.joinToString()}")
    result.appendLine()

    result.appendLine("===== Cluster Info =====")
    val cpuDir = File("/sys/devices/system/cpu/")
    val cores = cpuDir.listFiles { file ->
        file.name.matches(Regex("cpu[0-9]+"))
    } ?: emptyArray()

    val freqs = cores.map {
        read("${it.path}/cpufreq/cpuinfo_max_freq").toIntOrNull() ?: 0
    }

    val distinctFreqs = freqs.toSet().filter { it > 0 }

    if (distinctFreqs.size > 1) {
        result.appendLine("Detected Clusters: ${distinctFreqs.size}")
        result.appendLine("Likely Architecture: big.LITTLE")
    } else if (distinctFreqs.size == 1) {
        result.appendLine("Device has uniform cores.")
    } else {
        result.appendLine("Cluster info not available.")
    }

    return result.toString()
}

fun getCpuCoreInfo(): List<CpuCoreInfo> {
    val cpuDir = File("/sys/devices/system/cpu/")
    val cores = cpuDir.listFiles { file ->
        file.name.matches(Regex("cpu[0-9]+"))
    }?.sortedBy {
        it.name.removePrefix("cpu").toIntOrNull() ?: 0
    } ?: return emptyList()

    fun read(path: String): String {
        return try {
            File(path).readText().trim()
        } catch (_: Exception) {
            "N/A"
        }
    }

    return cores.map { core ->
        CpuCoreInfo(
            id = core.name,
            governor = read("${core.path}/cpufreq/scaling_governor"),
            minFreq = read("${core.path}/cpufreq/cpuinfo_min_freq"),
            maxFreq = read("${core.path}/cpufreq/cpuinfo_max_freq")
        )
    }
}

