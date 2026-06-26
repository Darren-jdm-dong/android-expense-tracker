package com.example.expense.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import java.time.LocalDateTime

data class MerchantMatch(
    val categoryId: Long,
    val matchCount: Int
)

@Dao
interface MerchantMappingDao {
    @Query("""
        INSERT INTO merchant_mappings (keyword, categoryId, matchCount, lastUsedAt, createdAt)
        VALUES (:keyword, :categoryId, 1, :now, :now)
        ON CONFLICT(keyword, categoryId)
        DO UPDATE SET matchCount = matchCount + 1, lastUsedAt = :now
    """)
    suspend fun upsert(keyword: String, categoryId: Long, now: LocalDateTime = LocalDateTime.now())

    @Query("""
        SELECT categoryId, matchCount
        FROM merchant_mappings
        WHERE keyword = :keyword
        ORDER BY matchCount DESC
        LIMIT 1
    """)
    suspend fun getBestMatch(keyword: String): MerchantMatch?

    @Query("""
        SELECT categoryId, matchCount
        FROM merchant_mappings
        WHERE keyword = :keyword
    """)
    suspend fun getAllMatches(keyword: String): List<MerchantMatch>
}
