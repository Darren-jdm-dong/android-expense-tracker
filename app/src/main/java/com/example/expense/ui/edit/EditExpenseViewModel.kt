package com.example.expense.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense.data.db.entity.CategoryEntity
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.domain.usecase.UpdateExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

data class EditExpenseUiState(
    val expenseId: Long = 0,
    val amount: String = "",
    val selectedCategory: CategoryEntity? = null,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class EditExpenseViewModel @Inject constructor(
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditExpenseUiState())
    val uiState: StateFlow<EditExpenseUiState> = _uiState.asStateFlow()

    fun loadExpense(expenseId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val expense = expenseRepository.getExpenseById(expenseId)
                if (expense != null) {
                    val category = categoryRepository.getCategoryById(expense.categoryId)
                    val categories = categoryRepository.getAllCategories()
                    _uiState.value = _uiState.value.copy(
                        expenseId = expense.id,
                        amount = expense.amount.toPlainString(),
                        selectedCategory = category,
                        date = expense.date,
                        note = expense.note ?: "",
                        categories = categories,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "支出记录不存在"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun selectCategory(category: CategoryEntity) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun updateExpense() {
        val state = _uiState.value
        val amount = state.amount.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _uiState.value = state.copy(error = "请输入有效金额")
            return
        }
        if (state.selectedCategory == null) {
            _uiState.value = state.copy(error = "请选择分类")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            val result = updateExpenseUseCase(
                expenseId = state.expenseId,
                amount = amount,
                categoryId = state.selectedCategory.id,
                date = state.date,
                note = state.note.takeIf { it.isNotBlank() }
            )
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSaving = false, success = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
