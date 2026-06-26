package com.example.expense.data.repository

import com.example.expense.data.db.dao.RecurringExpenseDao
import com.example.expense.data.db.entity.RecurringExpenseEntity
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringExpenseRepository @Inject constructor(
    private val recurringExpenseDao: RecurringExpenseDao
) {
    suspend fun addRecurringExpense(recurring: RecurringExpenseEntity): Long {
        return recurringExpenseDao.insert(recurring)
    }

    suspend fun updateRecurringExpense(recurring: RecurringExpenseEntity) {
        recurringExpenseDao.update(recurring)
    }

    suspend fun cancelRecurringExpense(id: Long) {
        recurringExpenseDao.cancel(id)
    }

    suspend fun getActiveRecurringExpenses(): List<RecurringExpenseEntity> {
        return recurringExpenseDao.getActive()
    }

    suspend fun getRecurringExpenseById(id: Long): RecurringExpenseEntity? {
        return recurringExpenseDao.getById(id)
    }

    suspend fun updateLastGeneratedDate(id: Long, date: LocalDate) {
        recurringExpenseDao.updateLastGenerated(id, date)
    }
}
