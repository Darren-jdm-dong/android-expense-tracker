package com.example.expense.domain.usecase

import com.example.expense.data.db.dao.CategoryTotal
import com.example.expense.data.db.dao.DailyTotal
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.ExpenseRepository
import java.time.LocalDate
import javax.inject.Inject

data class CategoryReport(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val total: java.math.BigDecimal,
    val percentage: Double
)

data class ReportData(
    val categoryReports: List<CategoryReport>,
    val dailyTotals: List<DailyTotal>,
    val totalAmount: java.math.BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate
)

class GetReportUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): ReportData {
        val categoryTotals = expenseRepository.getCategoryTotals(startDate, endDate)
        val dailyTotals = expenseRepository.getDailyTotals(startDate, endDate)

        val totalAmount = categoryTotals.fold(java.math.BigDecimal.ZERO) { acc, ct ->
            acc + ct.total
        }

        val categoryReports = categoryTotals.map { ct ->
            val category = categoryRepository.getCategoryById(ct.categoryId)
            val percentage = if (totalAmount > java.math.BigDecimal.ZERO) {
                (ct.total.multiply(java.math.BigDecimal(100)))
                    .divide(totalAmount, 1, java.math.RoundingMode.HALF_UP)
                    .toDouble()
            } else {
                0.0
            }

            CategoryReport(
                categoryId = ct.categoryId,
                categoryName = category?.name ?: "未知",
                categoryIcon = category?.icon ?: "📦",
                categoryColor = category?.color ?: "#607D8B",
                total = ct.total,
                percentage = percentage
            )
        }.sortedByDescending { it.total }

        return ReportData(
            categoryReports = categoryReports,
            dailyTotals = dailyTotals,
            totalAmount = totalAmount,
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getCurrentMonthReport(): ReportData {
        val now = LocalDate.now()
        val startDate = now.withDayOfMonth(1)
        val endDate = now
        return invoke(startDate, endDate)
    }
}
