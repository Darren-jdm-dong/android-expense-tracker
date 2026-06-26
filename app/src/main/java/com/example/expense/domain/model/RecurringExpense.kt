package com.example.expense.domain.model

import java.math.BigDecimal
import java.time.LocalDate

enum class Frequency {
    DAILY, WEEKLY, MONTHLY
}

data class RecurringExpense(
    val id: Long = 0,
    val amount: BigDecimal,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val note: String? = null,
    val currency: String = "CNY",
    val frequency: Frequency,
    val dayOfMonth: Int? = null,
    val dayOfWeek: Int? = null,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val isActive: Boolean = true,
    val lastGeneratedDate: LocalDate? = null
) {
    val formattedAmount: String
        get() = "¥${amount.setScale(2)}"

    val frequencyText: String
        get() = when (frequency) {
            Frequency.DAILY -> "每天"
            Frequency.WEEKLY -> "每周"
            Frequency.MONTHLY -> "每月"
        }
}
