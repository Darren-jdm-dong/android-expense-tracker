package com.example.expense.domain.model

import java.math.BigDecimal

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val categoryColor: String = "",
    val amount: BigDecimal,
    val yearMonth: String,
    val spentAmount: BigDecimal = BigDecimal.ZERO
) {
    val remainingAmount: BigDecimal
        get() = amount - spentAmount

    val usagePercent: BigDecimal
        get() = if (amount > BigDecimal.ZERO) {
            (spentAmount.multiply(BigDecimal(100))).divide(amount, 1, java.math.RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

    val isExceeded: Boolean
        get() = spentAmount > amount

    val isWarning: Boolean
        get() = usagePercent >= BigDecimal(80)

    val formattedAmount: String
        get() = "¥${amount.setScale(2)}"

    val formattedSpent: String
        get() = "¥${spentAmount.setScale(2)}"

    val formattedRemaining: String
        get() = "¥${remainingAmount.setScale(2)}"
}
