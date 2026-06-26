package com.example.expense.di

import android.content.Context
import androidx.room.Room
import com.example.expense.data.db.AppDatabase
import com.example.expense.data.db.dao.BudgetDao
import com.example.expense.data.db.dao.CategoryDao
import com.example.expense.data.db.dao.ExpenseDao
import com.example.expense.data.db.dao.MerchantMappingDao
import com.example.expense.data.db.dao.RecurringExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "expense_database"
        ).build()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    fun provideRecurringExpenseDao(database: AppDatabase): RecurringExpenseDao {
        return database.recurringExpenseDao()
    }

    @Provides
    fun provideMerchantMappingDao(database: AppDatabase): MerchantMappingDao {
        return database.merchantMappingDao()
    }
}
