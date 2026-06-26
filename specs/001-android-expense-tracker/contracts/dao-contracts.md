# DAO Contracts: Android Smart Expense Tracker

**Date**: 2026-06-24
**Feature**: Android Smart Expense Tracker

## Overview

This document defines the data access contracts (DAO interfaces) for the
Room database. These contracts specify the queries and operations available
for each entity.

## ExpenseDao

### Operations

```kotlin
// Insert a new expense
@Insert
suspend fun insert(expense: ExpenseEntity): Long

// Update an existing expense
@Update
suspend fun update(expense: ExpenseEntity)

// Soft delete (set isDeleted = true)
@Query("UPDATE expenses SET isDeleted = 1, updatedAt = :now WHERE id = :id")
suspend fun softDelete(id: Long, now: DateTime)

// Get single expense by ID
@Query("SELECT * FROM expenses WHERE id = :id AND isDeleted = 0")
suspend fun getById(id: Long): ExpenseEntity?

// Get expenses for a date range (for reports)
@Query("""
    SELECT * FROM expenses
    WHERE isDeleted = 0
    AND date BETWEEN :startDate AND :endDate
    ORDER BY date DESC, createdAt DESC
""")
suspend fun getByDateRange(startDate: Date, endDate: Date): List<ExpenseEntity>

// Get today's expenses (for home screen)
@Query("""
    SELECT * FROM expenses
    WHERE isDeleted = 0 AND date = :today
    ORDER BY createdAt DESC
""")
suspend fun getTodayExpenses(today: Date): List<ExpenseEntity>

// Get expenses grouped by category for a date range (for pie chart)
@Query("""
    SELECT categoryId, SUM(amount) as total
    FROM expenses
    WHERE isDeleted = 0
    AND date BETWEEN :startDate AND :endDate
    GROUP BY categoryId
    ORDER BY total DESC
""")
suspend fun getTotalByCategory(startDate: Date, endDate: Date): List<CategoryTotal>

// Get daily totals for a date range (for line chart)
@Query("""
    SELECT date, SUM(amount) as total
    FROM expenses
    WHERE isDeleted = 0
    AND date BETWEEN :startDate AND :endDate
    GROUP BY date
    ORDER BY date ASC
""")
suspend fun getDailyTotals(startDate: Date, endDate: Date): List<DailyTotal>

// Get total spending for a category in a month (for budget check)
@Query("""
    SELECT COALESCE(SUM(amount), 0)
    FROM expenses
    WHERE isDeleted = 0
    AND categoryId = :categoryId
    AND date BETWEEN :monthStart AND :monthEnd
""")
suspend fun getCategoryTotalForMonth(
    categoryId: Long,
    monthStart: Date,
    monthEnd: Date
): Decimal

// Export all expenses (for CSV export)
@Query("""
    SELECT * FROM expenses
    WHERE isDeleted = 0
    ORDER BY date DESC, createdAt DESC
""")
suspend fun getAllForExport(): List<ExpenseEntity>
```

### Data Classes

```kotlin
data class CategoryTotal(
    val categoryId: Long,
    val total: BigDecimal
)

data class DailyTotal(
    val date: Date,
    val total: BigDecimal
)
```

---

## CategoryDao

### Operations

```kotlin
// Insert category
@Insert
suspend fun insert(category: CategoryEntity): Long

// Update category
@Update
suspend fun update(category: CategoryEntity)

// Get all active categories
@Query("SELECT * FROM categories WHERE isHidden = 0 ORDER BY sortOrder ASC")
suspend fun getAll(): List<CategoryEntity>

// Get category by ID
@Query("SELECT * FROM categories WHERE id = :id")
suspend fun getById(id: Long): CategoryEntity?

// Get category by name (for duplicate check)
@Query("SELECT * FROM categories WHERE LOWER(name) = LOWER(:name)")
suspend fun getByName(name: String): CategoryEntity?

// Hide category (soft delete)
@Query("UPDATE categories SET isHidden = 1 WHERE id = :id AND isDefault = 0")
suspend fun hide(id: Long)

// Seed default categories
@Insert
suspend fun insertAll(categories: List<CategoryEntity>)
```

---

## BudgetDao

### Operations

```kotlin
// Insert or update budget (upsert)
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsert(budget: BudgetEntity)

// Get budget for a category and month
@Query("""
    SELECT * FROM budgets
    WHERE categoryId = :categoryId AND yearMonth = :yearMonth
""")
suspend fun get(categoryId: Long, yearMonth: String): BudgetEntity?

// Get all budgets for a month
@Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth")
suspend fun getAllForMonth(yearMonth: String): List<BudgetEntity>

// Delete budget
@Query("DELETE FROM budgets WHERE id = :id")
suspend fun delete(id: Long)
```

---

## RecurringExpenseDao

### Operations

```kotlin
// Insert recurring expense
@Insert
suspend fun insert(recurring: RecurringExpenseEntity): Long

// Update recurring expense
@Update
suspend fun update(recurring: RecurringExpenseEntity)

// Get all active recurring expenses
@Query("SELECT * FROM recurring_expenses WHERE isActive = 1")
suspend fun getActive(): List<RecurringExpenseEntity>

// Get recurring expense by ID
@Query("SELECT * FROM recurring_expenses WHERE id = :id")
suspend fun getById(id: Long): RecurringExpenseEntity?

// Cancel (deactivate) recurring expense
@Query("UPDATE recurring_expenses SET isActive = 0, updatedAt = :now WHERE id = :id")
suspend fun cancel(id: Long, now: DateTime)

// Update last generated date
@Query("""
    UPDATE recurring_expenses
    SET lastGeneratedDate = :date, updatedAt = :now
    WHERE id = :id
""")
suspend fun updateLastGenerated(id: Long, date: Date, now: DateTime)
```

---

## MerchantMappingDao

### Operations

```kotlin
// Insert or update mapping (upsert with count increment)
@Query("""
    INSERT INTO merchant_mappings (keyword, categoryId, matchCount, lastUsedAt, createdAt)
    VALUES (:keyword, :categoryId, 1, :now, :now)
    ON CONFLICT(keyword, categoryId)
    DO UPDATE SET matchCount = matchCount + 1, lastUsedAt = :now
""")
suspend fun upsert(keyword: String, categoryId: Long, now: DateTime)

// Get best category for a keyword
@Query("""
    SELECT categoryId, matchCount
    FROM merchant_mappings
    WHERE keyword = :keyword
    ORDER BY matchCount DESC
    LIMIT 1
""")
suspend fun getBestMatch(keyword: String): MerchantMatch?

// Get all mappings for a keyword (for confidence calculation)
@Query("""
    SELECT categoryId, matchCount
    FROM merchant_mappings
    WHERE keyword = :keyword
""")
suspend fun getAllMatches(keyword: String): List<MerchantMatch>
```

### Data Classes

```kotlin
data class MerchantMatch(
    val categoryId: Long,
    val matchCount: Int
)
```
