package com.example.expense.domain.usecase

import com.example.expense.data.repository.MerchantMappingRepository
import javax.inject.Inject

data class CategorySuggestion(
    val categoryId: Long,
    val confidence: Double,
    val autoSelect: Boolean
)

class CategorizeExpenseUseCase @Inject constructor(
    private val merchantMappingRepository: MerchantMappingRepository
) {
    suspend operator fun invoke(note: String): CategorySuggestion? {
        if (note.isBlank()) return null

        val keyword = note.lowercase().trim()
        val matches = merchantMappingRepository.getAllMatches(keyword)
        if (matches.isEmpty()) return null

        val totalMatches = matches.sumOf { it.matchCount }
        val bestMatch = matches.first()
        val confidence = bestMatch.matchCount.toDouble() / totalMatches

        return when {
            confidence >= 0.8 && bestMatch.matchCount >= 3 -> {
                CategorySuggestion(bestMatch.categoryId, confidence, autoSelect = true)
            }
            confidence >= 0.5 -> {
                CategorySuggestion(bestMatch.categoryId, confidence, autoSelect = false)
            }
            else -> null
        }
    }
}
