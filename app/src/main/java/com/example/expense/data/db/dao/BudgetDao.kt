package com.example.expense.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expense.data.db.entity.BudgetEntity

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: BudgetEntity)

    @Query("""
        SELECT * FROM budgets
        WHERE categoryId = :categoryId AND yearMonth = :yearMonth
    """)
    suspend fun get(categoryId: Long, yearMonth: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth")
    suspend fun getAllForMonth(yearMonth: String): List<BudgetEntity>

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun delete(id: Long)
}
