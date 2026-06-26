package com.example.expense.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense.data.db.entity.CategoryEntity
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.MerchantMappingRepository
import com.example.expense.domain.usecase.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

data class AddExpenseUiState(
    val amount: String = "",
    val selectedCategory: CategoryEntity? = null,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val currency: String = "CNY",
    val categories: List<CategoryEntity> = emptyList(),
    val suggestedCategory: CategoryEntity? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val categoryRepository: CategoryRepository,
    private val merchantMappingRepository: MerchantMappingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategories()
            _uiState.value = _uiState.value.copy(categories = categories)
        }
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun selectCategory(category: CategoryEntity) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
        checkCategorySuggestion(note)
    }

    fun updateCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(currency = currency)
    }

    private fun checkCategorySuggestion(note: String) {
        viewModelScope.launch {
            val suggestion = merchantMappingRepository.getCategorySuggestion(note)
            if (suggestion != null) {
                val category = categoryRepository.getCategoryById(suggestion.categoryId)
                _uiState.value = _uiState.value.copy(suggestedCategory = category)
                if (suggestion.autoSelect && _uiState.value.selectedCategory == null) {
                    _uiState.value = _uiState.value.copy(selectedCategory = category)
                }
            } else {
                _uiState.value = _uiState.value.copy(suggestedCategory = null)
            }
        }
    }

    fun saveExpense() {
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
            val result = addExpenseUseCase(
                amount = amount,
                categoryId = state.selectedCategory.id,
                date = state.date,
                note = state.note.takeIf { it.isNotBlank() },
                merchantName = state.note.takeIf { it.isNotBlank() },
                currency = state.currency
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
