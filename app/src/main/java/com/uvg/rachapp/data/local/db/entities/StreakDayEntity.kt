package com.uvg.rachapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marked_days")
data class StreakDayEntity(
    @PrimaryKey val date: String, // Formato "YYYY-MM-DD"
/*
    val day: Int,
    val month: Int,
    val year: Int,
    val isMarked: Boolean,
    val streakCount: Int,
    val longestStreak: Int,
    val lastMarkedDate: String,
    val currentStreak: Int,
    val totalMarkedDays: Int,
    val userId: String,
    val id: Int? = null

 */
)