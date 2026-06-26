package com.example.expense.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val isDefault: Boolean = false,
    val parentId: Long? = null,
    val sortOrder: Int = 0
)
