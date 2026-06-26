package com.example.expense.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "merchant_mappings",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["keyword", "categoryId"], unique = true),
        Index(value = ["keyword"])
    ]
)
data class MerchantMappingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String, // Stored lowercase
    val categoryId: Long,
    val matchCount: Int = 1,
    val lastUsedAt: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now()
)
