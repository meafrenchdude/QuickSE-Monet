package com.meafrenchdude.quickse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class SelinuxViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelinuxUiState())
    val uiState: StateFlow<SelinuxUiState> = _uiState.asStateFlow()

    fun checkRootAccess() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec("su")
                val output = process.outputStream
                output.write("exit\n".toByteArray())
                output.flush()
                process.waitFor()

                _uiState.value = _uiState.value.copy(
                    isRootAvailable = process.exitValue() == 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRootAvailable = false,
                )
            }
        }
    }

    fun getSelinuxStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "getenforce"))
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val status = reader.readLine()?.trim() ?: "Unknown"
                process.waitFor()

                _uiState.value = _uiState.value.copy(
                    status = status
                )
            } catch (e: Exception) {
                if (_uiState.value.isRootAvailable) {
                    _uiState.value = _uiState.value.copy(
                        status = "Unknown",
                        message = "Failed to get SELinux status: ${e.message}"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = "Unknown"
                    )
                }
            }
        }
    }

    fun toggleSelinuxMode() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newMode = if (_uiState.value.status.equals("Enforcing", true)) "0" else "1"
                val process = Runtime.getRuntime().exec("su")
                val output = process.outputStream
                output.write("setenforce $newMode\n".toByteArray())
                output.write("exit\n".toByteArray())
                output.flush()
                process.waitFor()

                if (process.exitValue() == 0) {
                    getSelinuxStatus()
                    _uiState.value = _uiState.value.copy(
                        message = "Mode changed successfully!"
                    )
                } else {
                    val error = BufferedReader(InputStreamReader(process.errorStream)).readLine()
                    _uiState.value = _uiState.value.copy(
                        message = "Failed: ${error ?: "Unknown error"}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class SelinuxUiState(
    val status: String? = null,
    val isRootAvailable: Boolean = false,
    val message: String? = null
)
