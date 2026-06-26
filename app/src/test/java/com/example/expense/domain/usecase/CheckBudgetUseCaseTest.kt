package com.example.expense.domain.usecase

import com.example.expense.data.db.entity.BudgetEntity
import com.example.expense.data.db.entity.CategoryEntity
import com.example.expense.data.repository.BudgetRepository
import com.example.expense.data.repository.CategoryRepository
import com.example.expense.data.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CheckBudgetUseCaseTest {

    private lateinit var budgetRepository: BudgetRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var checkBudgetUseCase: CheckBudgetUseCase

    @Before
    fun setup() {
        budgetRepository = mockk()
        categoryRepository = mockk()
        expenseRepository = mockk()
        checkBudgetUseCase = CheckBudgetUseCase(budgetRepository, categoryRepository, expenseRepository)
    }

    @Test
    fun `checkAllBudgets should return budget statuses`() = runTest {
        // Given
        val yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val category = CategoryEntity(
            id = 1,
            name = "餐饮",
            icon = "🍽️",
            color = "#FF5722",
            isDefault = true,
            sortOrder = 1
        )
        val budget = BudgetEntity(
            id = 1,
            categoryId = 1L,
            amount = BigDecimal("2000.00"),
            yearMonth = yearMonth
        )

        coEvery { budgetRepository.getBudgetsForMonth(yearMonth) } returns listOf(budget)
        coEvery { categoryRepository.getCategoryById(1L) } returns category
        coEvery { expenseRepository.getCategoryTotalForMonth(any(), any(), any()) } returns BigDecimal("1600.00")

        // When
        val result = checkBudgetUseCase.checkAllBudgets(yearMonth)

        // Then
        assertEquals(1, result.size)
        assertEquals("餐饮", result[0].categoryName)
        assertEquals(BigDecimal("2000.00"), result[0].budgetAmount)
        assertEquals(BigDecimal("1600.00"), result[0].spentAmount)
        assertTrue(result[0].isWarning) // 80% threshold
        assertFalse(result[0].isExceeded)
    }

    @Test
    fun `checkAllBudgets with exceeded budget should mark as exceeded`() = runTest {
        // Given
        val yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val category = CategoryEntity(
            id = 1,
            name = "餐饮",
            icon = "🍽️",
            color = "#FF5722",
            isDefault = true,
            sortOrder = 1
        )
        val budget = BudgetEntity(
            id = 1,
            categoryId = 1L,
            amount = BigDecimal("2000.00"),
            yearMonth = yearMonth
        )

        coEvery { budgetRepository.getBudgetsForMonth(yearMonth) } returns listOf(budget)
        coEvery { categoryRepository.getCategoryById(1L) } returns category
        coEvery { expenseRepository.getCategoryTotalForMonth(any(), any(), any()) } returns BigDecimal("2500.00")

        // When
        val result = checkBudgetUseCase.checkAllBudgets(yearMonth)

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isExceeded)
        assertTrue(result[0].isWarning)
    }

    @Test
    fun `checkAllBudgets with no budgets should return empty list`() = runTest {
        // Given
        val yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

        coEvery { budgetRepository.getBudgetsForMonth(yearMonth) } returns emptyList()

        // When
        val result = checkBudgetUseCase.checkAllBudgets(yearMonth)

        // Then
        assertTrue(result.isEmpty())
    }
}
