package com.example.expense.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expense.data.db.dao.BudgetDao
import com.example.expense.data.db.dao.CategoryDao
import com.example.expense.data.db.dao.ExpenseDao
import com.example.expense.data.db.dao.MerchantMappingDao
import com.example.expense.data.db.dao.RecurringExpenseDao
import com.example.expense.data.db.entity.BudgetEntity
import com.example.expense.data.db.entity.CategoryEntity
import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.data.db.entity.MerchantMappingEntity
import com.example.expense.data.db.entity.RecurringExpenseEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        RecurringExpenseEntity::class,
        MerchantMappingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao
    abstract fun merchantMappingDao(): MerchantMappingDao
}
