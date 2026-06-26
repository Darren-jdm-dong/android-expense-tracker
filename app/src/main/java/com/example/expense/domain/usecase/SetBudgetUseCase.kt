package com.example.expense.domain.usecase

import com.example.expense.data.repository.BudgetRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SetBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(
        categoryId: Long,
        amount: BigDecimal,
        yearMonth: String? = null
    ): Result<Long> {
        return try {
            if (amount <= BigDecimal.ZERO) {
                return Result.failure(IllegalArgumentException("预算金额必须大于0"))
            }

            val month = yearMonth ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            budgetRepository.setBudget(categoryId, amount, month)
            Result.success(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
