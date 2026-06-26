package com.example.expense.domain.usecase

import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.data.repository.MerchantMappingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class AddExpenseUseCaseTest {

    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var merchantMappingRepository: MerchantMappingRepository
    private lateinit var addExpenseUseCase: AddExpenseUseCase

    @Before
    fun setup() {
        expenseRepository = mockk()
        merchantMappingRepository = mockk()
        addExpenseUseCase = AddExpenseUseCase(expenseRepository, merchantMappingRepository)
    }

    @Test
    fun `invoke with valid data should return success`() = runTest {
        // Given
        val amount = BigDecimal("50.00")
        val categoryId = 1L
        val date = LocalDate.now()
        val note = "午餐"

        coEvery { expenseRepository.addExpense(any()) } returns 1L
        coEvery { merchantMappingRepository.saveMapping(any(), any()) } returns Unit

        // When
        val result = addExpenseUseCase(amount, categoryId, date, note)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { expenseRepository.addExpense(any()) }
        coVerify { merchantMappingRepository.saveMapping("午餐", categoryId) }
    }

    @Test
    fun `invoke with negative amount should return failure`() = runTest {
        // Given
        val amount = BigDecimal("-50.00")
        val categoryId = 1L
        val date = LocalDate.now()

        // When
        val result = addExpenseUseCase(amount, categoryId, date)

        // Then
        assertTrue(result.isFailure)
        assertEquals("金额必须大于0", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with future date should return failure`() = runTest {
        // Given
        val amount = BigDecimal("50.00")
        val categoryId = 1L
        val date = LocalDate.now().plusDays(1)

        // When
        val result = addExpenseUseCase(amount, categoryId, date)

        // Then
        assertTrue(result.isFailure)
        assertEquals("日期不能是未来日期", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with amount exceeding max should return failure`() = runTest {
        // Given
        val amount = BigDecimal("100000000.00")
        val categoryId = 1L
        val date = LocalDate.now()

        // When
        val result = addExpenseUseCase(amount, categoryId, date)

        // Then
        assertTrue(result.isFailure)
        assertEquals("金额不能超过99,999,999.99", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke without note should not save merchant mapping`() = runTest {
        // Given
        val amount = BigDecimal("50.00")
        val categoryId = 1L
        val date = LocalDate.now()

        coEvery { expenseRepository.addExpense(any()) } returns 1L

        // When
        val result = addExpenseUseCase(amount, categoryId, date)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { merchantMappingRepository.saveMapping(any(), any()) }
    }
}
