package com.example.expense.data.repository

import com.example.expense.data.db.dao.CategoryTotal
import com.example.expense.data.db.dao.DailyTotal
import com.example.expense.data.db.dao.ExpenseDao
import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.domain.model.Expense
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val categoryRepository: CategoryRepository
) {
    suspend fun addExpense(expense: ExpenseEntity): Long {
        return expenseDao.insert(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.update(expense)
    }

    suspend fun deleteExpense(id: Long) {
        expenseDao.softDelete(id)
    }

    suspend fun getExpenseById(id: Long): ExpenseEntity? {
        return expenseDao.getById(id)
    }

    suspend fun getTodayExpenses(): List<Expense> {
        val expenses = expenseDao.getTodayExpenses()
        return expenses.map { entity ->
            val category = categoryRepository.getCategoryById(entity.categoryId)
            Expense(
                id = entity.id,
                amount = entity.amount,
                categoryId = entity.categoryId,
                categoryName = category?.name ?: "",
                categoryIcon = category?.icon ?: "",
                categoryColor = category?.color ?: "",
                date = entity.date,
                note = entity.note,
                merchantName = entity.merchantName,
                currency = entity.currency,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    suspend fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): List<Expense> {
        val expenses = expenseDao.getByDateRange(startDate, endDate)
        return expenses.map { entity ->
            val category = categoryRepository.getCategoryById(entity.categoryId)
            Expense(
                id = entity.id,
                amount = entity.amount,
                categoryId = entity.categoryId,
                categoryName = category?.name ?: "",
                categoryIcon = category?.icon ?: "",
                categoryColor = category?.color ?: "",
                date = entity.date,
                note = entity.note,
                merchantName = entity.merchantName,
                currency = entity.currency,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    suspend fun getCategoryTotals(startDate: LocalDate, endDate: LocalDate): List<CategoryTotal> {
        return expenseDao.getTotalByCategory(startDate, endDate)
    }

    suspend fun getDailyTotals(startDate: LocalDate, endDate: LocalDate): List<DailyTotal> {
        return expenseDao.getDailyTotals(startDate, endDate)
    }

    suspend fun getCategoryTotalForMonth(categoryId: Long, monthStart: LocalDate, monthEnd: LocalDate): BigDecimal {
        return expenseDao.getCategoryTotalForMonth(categoryId, monthStart, monthEnd)
    }

    suspend fun getAllForExport(): List<ExpenseEntity> {
        return expenseDao.getAllForExport()
    }
}
