package com.example.expense.domain.usecase

import com.example.expense.data.repository.BudgetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class SetBudgetUseCaseTest {

    private lateinit var budgetRepository: BudgetRepository
    private lateinit var setBudgetUseCase: SetBudgetUseCase

    @Before
    fun setup() {
        budgetRepository = mockk()
        setBudgetUseCase = SetBudgetUseCase(budgetRepository)
    }

    @Test
    fun `invoke with valid data should return success`() = runTest {
        // Given
        val categoryId = 1L
        val amount = BigDecimal("2000.00")

        coEvery { budgetRepository.setBudget(any(), any(), any()) } returns 1L

        // When
        val result = setBudgetUseCase(categoryId, amount)

        // Then
        assertTrue(result.isSuccess)
        coVerify { budgetRepository.setBudget(categoryId, amount, any()) }
    }

    @Test
    fun `invoke with negative amount should return failure`() = runTest {
        // Given
        val categoryId = 1L
        val amount = BigDecimal("-100.00")

        // When
        val result = setBudgetUseCase(categoryId, amount)

        // Then
        assertTrue(result.isFailure)
        assertEquals("预算金额必须大于0", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with zero amount should return failure`() = runTest {
        // Given
        val categoryId = 1L
        val amount = BigDecimal("0")

        // When
        val result = setBudgetUseCase(categoryId, amount)

        // Then
        assertTrue(result.isFailure)
        assertEquals("预算金额必须大于0", result.exceptionOrNull()?.message)
    }
}
