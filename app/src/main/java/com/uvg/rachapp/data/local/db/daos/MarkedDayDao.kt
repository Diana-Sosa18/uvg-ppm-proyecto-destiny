package com.uvg.rachapp.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uvg.rachapp.data.local.db.entities.StreakDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkedDayDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkedDay(markedDay: StreakDayEntity)

    @Query("DELETE FROM marked_days WHERE date = :date")
    suspend fun deleteMarkedDayByDate(date: String)

    @Query("SELECT * FROM marked_days")
    fun getAllMarkedDays(): Flow<List<StreakDayEntity>>

}