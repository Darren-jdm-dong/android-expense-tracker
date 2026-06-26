package com.example.expense.data.repository

import com.example.expense.data.db.dao.CategoryDao
import com.example.expense.data.db.entity.CategoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun getAllCategories(): List<CategoryEntity> {
        return categoryDao.getAll()
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return categoryDao.getById(id)
    }

    suspend fun getCategoryByName(name: String): CategoryEntity? {
        return categoryDao.getByName(name)
    }

    suspend fun addCategory(category: CategoryEntity): Long {
        return categoryDao.insert(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.update(category)
    }

    suspend fun hideCategory(id: Long) {
        categoryDao.hide(id)
    }

    suspend fun seedDefaultCategories() {
        if (categoryDao.count() > 0) return

        val defaultCategories = listOf(
            CategoryEntity(name = "餐饮", icon = "🍽️", color = "#FF5722", isDefault = true, sortOrder = 1),
            CategoryEntity(name = "交通", icon = "🚗", color = "#2196F3", isDefault = true, sortOrder = 2),
            CategoryEntity(name = "购物", icon = "🛒", color = "#9C27B0", isDefault = true, sortOrder = 3),
            CategoryEntity(name = "住房", icon = "🏠", color = "#4CAF50", isDefault = true, sortOrder = 4),
            CategoryEntity(name = "娱乐", icon = "🎮", color = "#FF9800", isDefault = true, sortOrder = 5),
            CategoryEntity(name = "医疗", icon = "🏥", color = "#F44336", isDefault = true, sortOrder = 6),
            CategoryEntity(name = "教育", icon = "📚", color = "#00BCD4", isDefault = true, sortOrder = 7),
            CategoryEntity(name = "其他", icon = "📦", color = "#607D8B", isDefault = true, sortOrder = 8)
        )
        categoryDao.insertAll(defaultCategories)
    }
}
