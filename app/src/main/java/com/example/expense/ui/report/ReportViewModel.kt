package com.example.expense.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense.domain.usecase.CategoryReport
import com.example.expense.domain.usecase.GetReportUseCase
import com.example.expense.domain.usecase.ReportData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

data class ReportUiState(
    val reportData: ReportData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val selectedTab: Int = 0 // 0 = pie chart, 1 = line chart
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getReportUseCase: GetReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val reportData = getReportUseCase(
                    _uiState.value.startDate,
                    _uiState.value.endDate
                )
                _uiState.value = _uiState.value.copy(
                    reportData = reportData,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateDateRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.value = _uiState.value.copy(
            startDate = startDate,
            endDate = endDate
        )
        loadReport()
    }

    fun selectTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
