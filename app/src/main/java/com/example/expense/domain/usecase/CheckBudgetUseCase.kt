package com.example.expense.domain.usecase

import com.example.expense.data.repository.BudgetRepository
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.domain.model.Budget
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BudgetStatus(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val budgetAmount: BigDecimal,
    val spentAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val usagePercent: BigDecimal,
    val isWarning: Boolean,
    val isExceeded: Boolean
)

class CheckBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend fun checkAllBudgets(yearMonth: String? = null): List<BudgetStatus> {
        val month = yearMonth ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val budgets = budgetRepository.getBudgetsForMonth(month)

        val now = LocalDate.now()
        val monthStart = LocalDate.parse("$month-01")
        val monthEnd = now

        return budgets.map { budget ->
            val category = categoryRepository.getCategoryById(budget.categoryId)
            val spent = expenseRepository.getCategoryTotalForMonth(
                budget.categoryId,
                monthStart,
                monthEnd
            )
            val remaining = budget.amount - spent
            val usagePercent = if (budget.amount > BigDecimal.ZERO) {
                spent.multiply(BigDecimal(100))
                    .divide(budget.amount, 1, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }

            BudgetStatus(
                categoryId = budget.categoryId,
                categoryName = category?.name ?: "未知",
                categoryIcon = category?.icon ?: "📦",
                categoryColor = category?.color ?: "#607D8B",
                budgetAmount = budget.amount,
                spentAmount = spent,
                remainingAmount = remaining,
                usagePercent = usagePercent,
                isWarning = usagePercent >= BigDecimal(80),
                isExceeded = spent > budget.amount
            )
        }
    }

    suspend fun checkCategoryBudget(categoryId: Long, yearMonth: String? = null): BudgetStatus? {
        val month = yearMonth ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val budget = budgetRepository.getBudget(categoryId, month) ?: return null

        val now = LocalDate.now()
        val monthStart = LocalDate.parse("$month-01")
        val monthEnd = now

        val category = categoryRepository.getCategoryById(budget.categoryId)
        val spent = expenseRepository.getCategoryTotalForMonth(
            budget.categoryId,
            monthStart,
            monthEnd
        )
        val remaining = budget.amount - spent
        val usagePercent = if (budget.amount > BigDecimal.ZERO) {
            spent.multiply(BigDecimal(100))
                .divide(budget.amount, 1, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        return BudgetStatus(
            categoryId = budget.categoryId,
            categoryName = category?.name ?: "未知",
            categoryIcon = category?.icon ?: "📦",
            categoryColor = category?.color ?: "#607D8B",
            budgetAmount = budget.amount,
            spentAmount = spent,
            remainingAmount = remaining,
            usagePercent = usagePercent,
            isWarning = usagePercent >= BigDecimal(80),
            isExceeded = spent > budget.amount
        )
    }
}
