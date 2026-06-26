package com.example.expense.domain.usecase

import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.data.db.entity.RecurringExpenseEntity
import com.example.expense.data.db.entity.RecurringFrequency
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.data.repository.RecurringExpenseRepository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ManageRecurringUseCase @Inject constructor(
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend fun getActiveRecurringExpenses(): List<RecurringExpenseEntity> {
        return recurringExpenseRepository.getActiveRecurringExpenses()
    }

    suspend fun cancelRecurringExpense(id: Long) {
        recurringExpenseRepository.cancelRecurringExpense(id)
    }

    suspend fun generateDueExpenses(): List<Long> {
        val activeExpenses = recurringExpenseRepository.getActiveRecurringExpenses()
        val today = LocalDate.now()
        val generatedIds = mutableListOf<Long>()

        for (recurring in activeExpenses) {
            if (isDueToday(recurring, today)) {
                val expenseId = generateExpense(recurring, today)
                if (expenseId != null) {
                    recurringExpenseRepository.updateLastGeneratedDate(recurring.id, today)
                    generatedIds.add(expenseId)
                }
            }
        }

        return generatedIds
    }

    private fun isDueToday(recurring: RecurringExpenseEntity, today: LocalDate): Boolean {
        // Check if end date has passed
        if (recurring.endDate != null && today.isAfter(recurring.endDate)) {
            return false
        }

        // Check if already generated today
        if (recurring.lastGeneratedDate == today) {
            return false
        }

        return when (recurring.frequency) {
            RecurringFrequency.DAILY -> true
            RecurringFrequency.WEEKLY -> {
                recurring.dayOfWeek == today.dayOfWeek.value
            }
            RecurringFrequency.MONTHLY -> {
                recurring.dayOfMonth == today.dayOfMonth
            }
        }
    }

    private suspend fun generateExpense(recurring: RecurringExpenseEntity, date: LocalDate): Long? {
        return try {
            val expense = ExpenseEntity(
                amount = recurring.amount,
                categoryId = recurring.categoryId,
                date = date,
                note = recurring.note,
                currency = recurring.currency
            )
            expenseRepository.addExpense(expense)
        } catch (e: Exception) {
            null
        }
    }
}
