package com.example.expense.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SettingsUiState(
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val exportError: String? = null,
    val appLockEnabled: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportToCsv(outputDir: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportError = null)
            try {
                val expenses = expenseRepository.getAllForExport()
                val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                val file = File(outputDir, "expenses_$timestamp.csv")

                file.bufferedWriter().use { writer ->
                    writer.write("日期,金额,分类,备注,货币")
                    writer.newLine()
                    expenses.forEach { expense ->
                        writer.write("${expense.date},${expense.amount},${expense.categoryId},${expense.note ?: ""},${expense.currency}")
                        writer.newLine()
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportError = e.message
                )
            }
        }
    }

    fun toggleAppLock(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(appLockEnabled = enabled)
    }
}
