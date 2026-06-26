package com.example.expense.di

import com.example.expense.data.db.dao.BudgetDao
import com.example.expense.data.db.dao.CategoryDao
import com.example.expense.data.db.dao.ExpenseDao
import com.example.expense.data.db.dao.MerchantMappingDao
import com.example.expense.data.db.dao.RecurringExpenseDao
import com.example.expense.data.repository.BudgetRepository
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.data.repository.MerchantMappingRepository
import com.example.expense.data.repository.RecurringExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
        categoryRepository: CategoryRepository
    ): ExpenseRepository {
        return ExpenseRepository(expenseDao, categoryRepository)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepository(categoryDao)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(budgetDao: BudgetDao): BudgetRepository {
        return BudgetRepository(budgetDao)
    }

    @Provides
    @Singleton
    fun provideRecurringExpenseRepository(recurringExpenseDao: RecurringExpenseDao): RecurringExpenseRepository {
        return RecurringExpenseRepository(recurringExpenseDao)
    }

    @Provides
    @Singleton
    fun provideMerchantMappingRepository(merchantMappingDao: MerchantMappingDao): MerchantMappingRepository {
        return MerchantMappingRepository(merchantMappingDao)
    }
}
