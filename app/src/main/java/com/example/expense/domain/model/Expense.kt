package com.example.expense.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val amount: BigDecimal,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val categoryColor: String = "",
    val date: LocalDate,
    val note: String? = null,
    val merchantName: String? = null,
    val currency: String = "CNY",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val formattedAmount: String
        get() = "¥${amount.setScale(2)}"

    val formattedDate: String
        get() = "${date.year}年${date.monthValue}月${date.dayOfMonth}日"
}
