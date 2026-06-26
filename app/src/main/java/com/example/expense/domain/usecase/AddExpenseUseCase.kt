package com.example.expense.domain.usecase

import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.data.repository.MerchantMappingRepository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val merchantMappingRepository: MerchantMappingRepository
) {
    suspend operator fun invoke(
        amount: BigDecimal,
        categoryId: Long,
        date: LocalDate,
        note: String? = null,
        merchantName: String? = null,
        currency: String = "CNY"
    ): Result<Long> {
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

            // Create expense entity
            val expense = ExpenseEntity(
                amount = amount,
                categoryId = categoryId,
                date = date,
                note = note,
                merchantName = merchantName,
                currency = currency
            )

            // Insert expense
            val expenseId = expenseRepository.addExpense(expense)

            // Save merchant mapping if note is provided
            if (!note.isNullOrBlank()) {
                merchantMappingRepository.saveMapping(note, categoryId)
            }

            Result.success(expenseId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
