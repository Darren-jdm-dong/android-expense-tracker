package com.example.expense.domain.usecase

import com.example.expense.data.db.entity.ExpenseEntity
import com.example.expense.data.db.entity.RecurringExpenseEntity
import com.example.expense.data.db.entity.RecurringFrequency
import com.example.expense.data.repository.ExpenseRepository
import com.example.expense.data.repository.RecurringExpenseRepository
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

class ManageRecurringUseCaseTest {

    private lateinit var recurringExpenseRepository: RecurringExpenseRepository
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var manageRecurringUseCase: ManageRecurringUseCase

    @Before
    fun setup() {
        recurringExpenseRepository = mockk()
        expenseRepository = mockk()
        manageRecurringUseCase = ManageRecurringUseCase(recurringExpenseRepository, expenseRepository)
    }

    @Test
    fun `getActiveRecurringExpenses should return active expenses`() = runTest {
        // Given
        val recurring = RecurringExpenseEntity(
            id = 1,
            amount = BigDecimal("3000.00"),
            categoryId = 1,
            note = "房租",
            frequency = RecurringFrequency.MONTHLY,
            dayOfMonth = 1,
            startDate = LocalDate.now().minusMonths(1),
            isActive = true
        )

        coEvery { recurringExpenseRepository.getActiveRecurringExpenses() } returns listOf(recurring)

        // When
        val result = manageRecurringUseCase.getActiveRecurringExpenses()

        // Then
        assertEquals(1, result.size)
        assertEquals("房租", result[0].note)
    }

    @Test
    fun `cancelRecurringExpense should deactivate expense`() = runTest {
        // Given
        coEvery { recurringExpenseRepository.cancelRecurringExpense(1L) } returns Unit

        // When
        manageRecurringUseCase.cancelRecurringExpense(1L)

        // Then
        coVerify { recurringExpenseRepository.cancelRecurringExpense(1L) }
    }

    @Test
    fun `generateDueExpenses should generate expenses for due recurring`() = runTest {
        // Given
        val recurring = RecurringExpenseEntity(
            id = 1,
            amount = BigDecimal("3000.00"),
            categoryId = 1,
            note = "房租",
            frequency = RecurringFrequency.DAILY,
            startDate = LocalDate.now().minusDays(1),
            isActive = true
        )

        coEvery { recurringExpenseRepository.getActiveRecurringExpenses() } returns listOf(recurring)
        coEvery { expenseRepository.addExpense(any()) } returns 1L
        coEvery { recurringExpenseRepository.updateLastGeneratedDate(any(), any()) } returns Unit

        // When
        val result = manageRecurringUseCase.generateDueExpenses()

        // Then
        assertEquals(1, result.size)
        coVerify { expenseRepository.addExpense(any()) }
        coVerify { recurringExpenseRepository.updateLastGeneratedDate(1L, LocalDate.now()) }
    }

    @Test
    fun `generateDueExpenses should not generate if already generated today`() = runTest {
        // Given
        val recurring = RecurringExpenseEntity(
            id = 1,
            amount = BigDecimal("3000.00"),
            categoryId = 1,
            note = "房租",
            frequency = RecurringFrequency.DAILY,
            startDate = LocalDate.now().minusDays(1),
            isActive = true,
            lastGeneratedDate = LocalDate.now()
        )

        coEvery { recurringExpenseRepository.getActiveRecurringExpenses() } returns listOf(recurring)

        // When
        val result = manageRecurringUseCase.generateDueExpenses()

        // Then
        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { expenseRepository.addExpense(any()) }
    }
}
