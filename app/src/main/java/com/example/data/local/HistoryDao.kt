package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity)

    @Delete
    suspend fun deleteHistory(item: HistoryEntity)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()

    // Favorites
    @Query("SELECT calculatorId FROM favorite_calculators")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorite_calculators WHERE calculatorId = :calculatorId")
    suspend fun removeFavorite(calculatorId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_calculators WHERE calculatorId = :calculatorId)")
    fun isFavorite(calculatorId: String): Flow<Boolean>
}
