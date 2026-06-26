package com.example.expense.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.domain.model.Expense
import com.example.expense.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class HomeUiState(
    val todayExpenses: List<Expense> = emptyList(),
    val todayTotal: BigDecimal = BigDecimal.ZERO,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodayExpenses()
    }

    fun loadTodayExpenses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Seed default categories if needed
                categoryRepository.seedDefaultCategories()

                val expenses = getExpensesUseCase.getTodayExpenses()
                val total = getExpensesUseCase.getTodayTotal()
                _uiState.value = _uiState.value.copy(
                    todayExpenses = expenses,
                    todayTotal = total,
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

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpense(id)
                loadTodayExpenses()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
