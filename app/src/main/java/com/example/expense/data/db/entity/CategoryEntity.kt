package com.example.expense.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val isDefault: Boolean = false,
    val isHidden: Boolean = false,
    val parentId: Long? = null,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
