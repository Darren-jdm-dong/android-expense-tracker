package com.example.expense.domain.usecase

import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.data.repository.ExpenseRepository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(
        expenseId: Long,
        amount: BigDecimal,
        categoryId: Long,
        date: LocalDate,
        note: String? = null,
        currency: String = "CNY"
    ): Result<Unit> {
        return try {
            // Validate amount
            if (amount <= BigDecimal.ZERO) {
                return Result.failure(IllegalArgumentException("金额必须大于0"))
            }
            if (amount > BigDecimal("99999999.99")) {
                return Result.failure(IllegalArgumentException("金额不能超过99,999,999.99"))
            }

            // Validate date (not in future)
            if (date.isAfter(LocalDate.now())) {
                return Result.failure(IllegalArgumentException("日期不能是未来日期"))
            }

            // Get existing expense
            val existingExpense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("支出记录不存在"))

            // Update expense
            val updatedExpense = existingExpense.copy(
                amount = amount,
                categoryId = categoryId,
                date = date,
                note = note,
                currency = currency,
                updatedAt = java.time.LocalDateTime.now()
            )

            expenseRepository.updateExpense(updatedExpense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
