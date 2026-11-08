package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val calculatorId: String,
    val title: String,
    val categoryName: String,
    val resultSummary: String,
    val inputsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_calculators")
data class FavoriteEntity(
    @PrimaryKey
    val calculatorId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)
