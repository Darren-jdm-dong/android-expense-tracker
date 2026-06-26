package com.example.expense.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expense.data.db.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE isHidden = 0 ORDER BY sortOrder ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE LOWER(name) = LOWER(:name)")
    suspend fun getByName(name: String): CategoryEntity?

    @Query("UPDATE categories SET isHidden = 1 WHERE id = :id AND isDefault = 0")
    suspend fun hide(id: Long)

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
