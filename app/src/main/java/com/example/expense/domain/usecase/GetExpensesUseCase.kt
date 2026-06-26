package com.example.expense.domain.usecase

import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.domain.model.Expense
import java.time.LocalDate
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend fun getTodayExpenses(): List<Expense> {
        return expenseRepository.getTodayExpenses()
    }

    suspend fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): List<Expense> {
        return expenseRepository.getExpensesByDateRange(startDate, endDate)
    }

    suspend fun getTodayTotal(): java.math.BigDecimal {
        val expenses = expenseRepository.getTodayExpenses()
        return expenses.fold(java.math.BigDecimal.ZERO) { acc, expense ->
            acc + expense.amount
        }
    }
}
