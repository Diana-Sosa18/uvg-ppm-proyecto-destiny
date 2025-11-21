package com.uvg.rachapp.data.repository

import com.uvg.rachapp.data.local.db.daos.MarkedDayDao
import com.uvg.rachapp.data.local.db.entities.StreakDayEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkedDayRepository @Inject constructor(
    private val dao: MarkedDayDao
) {
    fun getAllMarkedDays(): Flow<List<StreakDayEntity>> = dao.getAllMarkedDays()
    suspend fun insertMarkedDay(day: StreakDayEntity) = dao.insertMarkedDay(day)
    suspend fun deleteMarkedDayByDate(date: String) = dao.deleteMarkedDayByDate(date)

}