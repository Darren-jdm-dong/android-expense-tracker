package com.example.expense.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expense.data.db.entity.ExpenseEntity
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class CategoryTotal(
    val categoryId: Long,
    val total: BigDecimal
)

data class DailyTotal(
    val date: LocalDate,
    val total: BigDecimal
)

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("UPDATE expenses SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: LocalDateTime = LocalDateTime.now())

    @Query("SELECT * FROM expenses WHERE id = :id AND isDeleted = 0")
    suspend fun getById(id: Long): ExpenseEntity?

    @Query("""
        SELECT * FROM expenses
        WHERE isDeleted = 0
        AND date BETWEEN :startDate AND :endDate
        ORDER BY date DESC, createdAt DESC
    """)
    suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): List<ExpenseEntity>

    @Query("""
        SELECT * FROM expenses
        WHERE isDeleted = 0 AND date = :today
        ORDER BY createdAt DESC
    """)
    suspend fun getTodayExpenses(today: LocalDate = LocalDate.now()): List<ExpenseEntity>

    @Query("""
        SELECT categoryId, SUM(amount) as total
        FROM expenses
        WHERE isDeleted = 0
        AND date BETWEEN :startDate AND :endDate
        GROUP BY categoryId
        ORDER BY total DESC
    """)
    suspend fun getTotalByCategory(startDate: LocalDate, endDate: LocalDate): List<CategoryTotal>

    @Query("""
        SELECT date, SUM(amount) as total
        FROM expenses
        WHERE isDeleted = 0
        AND date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailyTotals(startDate: LocalDate, endDate: LocalDate): List<DailyTotal>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM expenses
        WHERE isDeleted = 0
        AND categoryId = :categoryId
        AND date BETWEEN :monthStart AND :monthEnd
    """)
    suspend fun getCategoryTotalForMonth(
        categoryId: Long,
        monthStart: LocalDate,
        monthEnd: LocalDate
    ): BigDecimal

    @Query("""
        SELECT * FROM expenses
        WHERE isDeleted = 0
        ORDER BY date DESC, createdAt DESC
    """)
    suspend fun getAllForExport(): List<ExpenseEntity>
}
