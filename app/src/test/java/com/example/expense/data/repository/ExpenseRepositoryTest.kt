package com.example.expense.data.repository

import com.example.expense.data.db.dao.ExpenseDao
import com.example.expense.data.db.entity.CategoryEntity
import com.example.expense.data.db.entity.ExpenseEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class ExpenseRepositoryTest {

    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var expenseRepository: ExpenseRepository

    @Before
    fun setup() {
        expenseDao = mockk()
        categoryRepository = mockk()
        expenseRepository = ExpenseRepository(expenseDao, categoryRepository)
    }

    @Test
    fun `addExpense should insert expense and return id`() = runTest {
        // Given
        val expense = ExpenseEntity(
            amount = BigDecimal("50.00"),
            categoryId = 1L,
            date = LocalDate.now(),
            note = "午餐"
        )

        coEvery { expenseDao.insert(any()) } returns 1L

        // When
        val result = expenseRepository.addExpense(expense)

        // Then
        assertEquals(1L, result)
        coVerify { expenseDao.insert(expense) }
    }

    @Test
    fun `deleteExpense should soft delete expense`() = runTest {
        // Given
        val expenseId = 1L

        coEvery { expenseDao.softDelete(any(), any()) } returns Unit

        // When
        expenseRepository.deleteExpense(expenseId)

        // Then
        coVerify { expenseDao.softDelete(expenseId, any()) }
    }

    @Test
    fun `getTodayExpenses should return expenses with category info`() = runTest {
        // Given
        val category = CategoryEntity(
            id = 1,
            name = "餐饮",
            icon = "🍽️",
            color = "#FF5722",
            isDefault = true,
            sortOrder = 1
        )
        val expenseEntity = ExpenseEntity(
            id = 1,
            amount = BigDecimal("50.00"),
            categoryId = 1L,
            date = LocalDate.now(),
            note = "午餐"
        )

        coEvery { expenseDao.getTodayExpenses(any()) } returns listOf(expenseEntity)
        coEvery { categoryRepository.getCategoryById(1L) } returns category

        // When
        val result = expenseRepository.getTodayExpenses()

        // Then
        assertEquals(1, result.size)
        assertEquals("餐饮", result[0].categoryName)
        assertEquals("🍽️", result[0].categoryIcon)
    }
}
