package com.example.expense.data.repository

import com.example.expense.data.db.dao.BudgetDao
import com.example.expense.data.db.entity.BudgetEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    suspend fun setBudget(categoryId: Long, amount: java.math.BigDecimal, yearMonth: String): Long {
        val budget = BudgetEntity(
            categoryId = categoryId,
            amount = amount,
            yearMonth = yearMonth
        )
        budgetDao.upsert(budget)
        return budget.id
    }

    suspend fun getBudget(categoryId: Long, yearMonth: String): BudgetEntity? {
        return budgetDao.get(categoryId, yearMonth)
    }

    suspend fun getBudgetsForMonth(yearMonth: String): List<BudgetEntity> {
        return budgetDao.getAllForMonth(yearMonth)
    }

    suspend fun deleteBudget(id: Long) {
        budgetDao.delete(id)
    }
}
