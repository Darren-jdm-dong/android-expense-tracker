package com.example.expense.domain.usecase

import com.example.expense.data.db.dao.MerchantMatch
import com.example.expense.data.repository.MerchantMappingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CategorizeExpenseUseCaseTest {

    private lateinit var merchantMappingRepository: MerchantMappingRepository
    private lateinit var categorizeExpenseUseCase: CategorizeExpenseUseCase

    @Before
    fun setup() {
        merchantMappingRepository = mockk()
        categorizeExpenseUseCase = CategorizeExpenseUseCase(merchantMappingRepository)
    }

    @Test
    fun `invoke with blank note should return null`() = runTest {
        // When
        val result = categorizeExpenseUseCase("")

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke with no matches should return null`() = runTest {
        // Given
        coEvery { merchantMappingRepository.getAllMatches("星巴克") } returns emptyList()

        // When
        val result = categorizeExpenseUseCase("星巴克")

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke with high confidence match should auto-select`() = runTest {
        // Given
        val matches = listOf(
            MerchantMatch(categoryId = 1L, matchCount = 5)
        )
        coEvery { merchantMappingRepository.getAllMatches("星巴克") } returns matches

        // When
        val result = categorizeExpenseUseCase("星巴克")

        // Then
        assertNotNull(result)
        assertEquals(1L, result!!.categoryId)
        assertTrue(result.autoSelect)
    }

    @Test
    fun `invoke with medium confidence should suggest but not auto-select`() = runTest {
        // Given
        val matches = listOf(
            MerchantMatch(categoryId = 1L, matchCount = 2),
            MerchantMatch(categoryId = 2L, matchCount = 1)
        )
        coEvery { merchantMappingRepository.getAllMatches("星巴克") } returns matches

        // When
        val result = categorizeExpenseUseCase("星巴克")

        // Then
        assertNotNull(result)
        assertEquals(1L, result!!.categoryId)
        assertFalse(result.autoSelect)
    }

    @Test
    fun `invoke with low confidence should return null`() = runTest {
        // Given
        val matches = listOf(
            MerchantMatch(categoryId = 1L, matchCount = 1),
            MerchantMatch(categoryId = 2L, matchCount = 1),
            MerchantMatch(categoryId = 3L, matchCount = 1)
        )
        coEvery { merchantMappingRepository.getAllMatches("未知商家") } returns matches

        // When
        val result = categorizeExpenseUseCase("未知商家")

        // Then
        assertNull(result)
    }
}
