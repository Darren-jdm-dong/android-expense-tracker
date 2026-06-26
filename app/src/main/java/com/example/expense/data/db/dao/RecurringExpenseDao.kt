package com.example.expense.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expense.data.db.entity.RecurringExpenseEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface RecurringExpenseDao {
    @Insert
    suspend fun insert(recurring: RecurringExpenseEntity): Long

    @Update
    suspend fun update(recurring: RecurringExpenseEntity)

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1")
    suspend fun getActive(): List<RecurringExpenseEntity>

    @Query("SELECT * FROM recurring_expenses WHERE id = :id")
    suspend fun getById(id: Long): RecurringExpenseEntity?

    @Query("UPDATE recurring_expenses SET isActive = 0, updatedAt = :now WHERE id = :id")
    suspend fun cancel(id: Long, now: LocalDateTime = LocalDateTime.now())

    @Query("""
        UPDATE recurring_expenses
        SET lastGeneratedDate = :date, updatedAt = :now
        WHERE id = :id
    """)
    suspend fun updateLastGenerated(id: Long, date: LocalDate, now: LocalDateTime = LocalDateTime.now())
}
