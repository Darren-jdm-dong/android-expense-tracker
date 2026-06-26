package com.example.expense.data.repository

import com.example.expense.data.db.dao.MerchantMappingDao
import com.example.expense.data.db.dao.MerchantMatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantMappingRepository @Inject constructor(
    private val merchantMappingDao: MerchantMappingDao
) {
    suspend fun saveMapping(keyword: String, categoryId: Long) {
        merchantMappingDao.upsert(keyword.lowercase(), categoryId)
    }

    suspend fun getBestMatch(keyword: String): MerchantMatch? {
        return merchantMappingDao.getBestMatch(keyword.lowercase())
    }

    suspend fun getAllMatches(keyword: String): List<MerchantMatch> {
        return merchantMappingDao.getAllMatches(keyword.lowercase())
    }

    suspend fun getCategorySuggestion(note: String): CategorySuggestion? {
        if (note.isBlank()) return null

        val keyword = note.lowercase().trim()
        val matches = getAllMatches(keyword)
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

data class CategorySuggestion(
    val categoryId: Long,
    val confidence: Double,
    val autoSelect: Boolean
)
