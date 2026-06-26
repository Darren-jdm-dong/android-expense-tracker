package com.example.expense.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "expenses",
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
        Index(value = ["date"]),
        Index(value = ["isDeleted"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: BigDecimal,
    val categoryId: Long,
    val date: LocalDate,
    val note: String? = null,
    val merchantName: String? = null,
    val currency: String = "CNY",
    val isDeleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
