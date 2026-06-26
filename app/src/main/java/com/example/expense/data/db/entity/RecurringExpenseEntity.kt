package com.example.expense.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

enum class RecurringFrequency {
    DAILY, WEEKLY, MONTHLY
}

@Entity(
    tableName = "recurring_expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["isActive"])
    ]
)
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: BigDecimal,
    val categoryId: Long,
    val note: String? = null,
    val currency: String = "CNY",
    val frequency: RecurringFrequency,
    val dayOfMonth: Int? = null, // For MONTHLY (1-28)
    val dayOfWeek: Int? = null, // For WEEKLY (1=Monday, 7=Sunday)
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val isActive: Boolean = true,
    val lastGeneratedDate: LocalDate? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
