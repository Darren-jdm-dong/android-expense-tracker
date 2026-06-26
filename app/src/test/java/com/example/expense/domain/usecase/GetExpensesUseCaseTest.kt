package com.example.expense.domain.usecase

import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.domain.model.Expense
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class GetExpensesUseCaseTest {

    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var getExpensesUseCase: GetExpensesUseCase

    @Before
    fun setup() {
        expenseRepository = mockk()
        getExpensesUseCase = GetExpensesUseCase(expenseRepository)
    }

    @Test
    fun `getTodayExpenses should return expenses for today`() = runTest {
        // Given
        val expenses = listOf(
            Expense(
                id = 1,
                amount = BigDecimal("50.00"),
                categoryId = 1,
                categoryName = "餐饮",
                categoryIcon = "🍽️",
                categoryColor = "#FF5722",
                date = LocalDate.now(),
                note = "午餐",
                createdAt = LocalDateTime.now()
            ),
            Expense(
                id = 2,
                amount = BigDecimal("30.00"),
                categoryId = 2,
                categoryName = "交通",
                categoryIcon = "🚗",
                categoryColor = "#2196F3",
                date = LocalDate.now(),
                note = "地铁",
                createdAt = LocalDateTime.now()
            )
        )

        coEvery { expenseRepository.getTodayExpenses() } returns expenses

        // When
        val result = getExpensesUseCase.getTodayExpenses()

        // Then
        assertEquals(2, result.size)
        assertEquals(BigDecimal("50.00"), result[0].amount)
        assertEquals(BigDecimal("30.00"), result[1].amount)
    }

    @Test
    fun `getTodayTotal should return sum of today's expenses`() = runTest {
        // Given
        val expenses = listOf(
            Expense(
                id = 1,
                amount = BigDecimal("50.00"),
                categoryId = 1,
                categoryName = "餐饮",
                categoryIcon = "🍽️",
                categoryColor = "#FF5722",
                date = LocalDate.now(),
                createdAt = LocalDateTime.now()
            ),
            Expense(
                id = 2,
                amount = BigDecimal("30.00"),
                categoryId = 2,
                categoryName = "交通",
                categoryIcon = "🚗",
                categoryColor = "#2196F3",
                date = LocalDate.now(),
                createdAt = LocalDateTime.now()
            )
        )

        coEvery { expenseRepository.getTodayExpenses() } returns expenses

        // When
        val result = getExpensesUseCase.getTodayTotal()

        // Then
        assertEquals(BigDecimal("80.00"), result)
    }

    @Test
    fun `getTodayTotal with no expenses should return zero`() = runTest {
        // Given
        coEvery { expenseRepository.getTodayExpenses() } returns emptyList()

        // When
        val result = getExpensesUseCase.getTodayTotal()

        // Then
        assertEquals(BigDecimal("0"), result)
    }
}
