package com.example.expense.domain.usecase

import com.example.expense.data.db.dao.CategoryTotal
import com.example.expense.data.db.dao.DailyTotal
import com.example.expense.data.db.entity.CategoryEntity
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

class GetReportUseCaseTest {

    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var getReportUseCase: GetReportUseCase

    @Before
    fun setup() {
        expenseRepository = mockk()
        categoryRepository = mockk()
        getReportUseCase = GetReportUseCase(expenseRepository, categoryRepository)
    }

    @Test
    fun `invoke should return report data with category totals`() = runTest {
        // Given
        val startDate = LocalDate.now().withDayOfMonth(1)
        val endDate = LocalDate.now()
        val category = CategoryEntity(
            id = 1,
            name = "餐饮",
            icon = "🍽️",
            color = "#FF5722",
            isDefault = true,
            sortOrder = 1
        )

        val categoryTotals = listOf(
            CategoryTotal(1L, BigDecimal("100.00"))
        )
        val dailyTotals = listOf(
            DailyTotal(LocalDate.now(), BigDecimal("100.00"))
        )

        coEvery { expenseRepository.getCategoryTotals(startDate, endDate) } returns categoryTotals
        coEvery { expenseRepository.getDailyTotals(startDate, endDate) } returns dailyTotals
        coEvery { categoryRepository.getCategoryById(1L) } returns category

        // When
        val result = getReportUseCase(startDate, endDate)

        // Then
        assertEquals(1, result.categoryReports.size)
        assertEquals("餐饮", result.categoryReports[0].categoryName)
        assertEquals(BigDecimal("100.00"), result.categoryReports[0].total)
        assertEquals(100.0, result.categoryReports[0].percentage, 0.1)
    }

    @Test
    fun `invoke with no data should return empty report`() = runTest {
        // Given
        val startDate = LocalDate.now().withDayOfMonth(1)
        val endDate = LocalDate.now()

        coEvery { expenseRepository.getCategoryTotals(startDate, endDate) } returns emptyList()
        coEvery { expenseRepository.getDailyTotals(startDate, endDate) } returns emptyList()

        // When
        val result = getReportUseCase(startDate, endDate)

        // Then
        assertTrue(result.categoryReports.isEmpty())
        assertTrue(result.dailyTotals.isEmpty())
        assertEquals(BigDecimal("0"), result.totalAmount)
    }

    @Test
    fun `getCurrentMonthReport should use current month dates`() = runTest {
        // Given
        val now = LocalDate.now()
        val startDate = now.withDayOfMonth(1)
        val endDate = now

        coEvery { expenseRepository.getCategoryTotals(startDate, endDate) } returns emptyList()
        coEvery { expenseRepository.getDailyTotals(startDate, endDate) } returns emptyList()

        // When
        val result = getReportUseCase.getCurrentMonthReport()

        // Then
        assertEquals(startDate, result.startDate)
        assertEquals(endDate, result.endDate)
    }
}
