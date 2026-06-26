package com.example.expense.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.domain.usecase.BudgetStatus
import com.example.expense.domain.usecase.CheckBudgetUseCase
import com.example.expense.domain.usecase.SetBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class BudgetUiState(
    val budgets: List<BudgetStatus> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val selectedCategoryId: Long? = null,
    val budgetAmount: String = ""
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val checkBudgetUseCase: CheckBudgetUseCase,
    private val setBudgetUseCase: SetBudgetUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadBudgets()
    }

    fun loadBudgets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val budgets = checkBudgetUseCase.checkAllBudgets()
                _uiState.value = _uiState.value.copy(
                    budgets = budgets,
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

    fun showAddDialog(categoryId: Long? = null) {
        _uiState.value = _uiState.value.copy(
            showAddDialog = true,
            selectedCategoryId = categoryId,
            budgetAmount = ""
        )
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun updateBudgetAmount(amount: String) {
        _uiState.value = _uiState.value.copy(budgetAmount = amount)
    }

    fun selectCategory(categoryId: Long) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun saveBudget() {
        val state = _uiState.value
        val amount = state.budgetAmount.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _uiState.value = state.copy(error = "请输入有效金额")
            return
        }
        if (state.selectedCategoryId == null) {
            _uiState.value = state.copy(error = "请选择分类")
            return
        }

        viewModelScope.launch {
            val result = setBudgetUseCase(state.selectedCategoryId, amount)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(showAddDialog = false)
                    loadBudgets()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
            )
        }
    }
}
