package com.uvg.rachapp.data.repository

import com.uvg.rachapp.data.local.db.daos.MarkedDayDao
import com.uvg.rachapp.data.local.db.entities.StreakDayEntity
import kotlinx.coroutines.flow.Flow



class MarkedDayRepository(private val dao: MarkedDayDao) {
    fun getAllMarkedDays(): Flow<List<StreakDayEntity>> = dao.getAllMarkedDays()

    suspend fun insertMarkedDay(day: StreakDayEntity) = dao.insertMarkedDay(day)
    suspend fun deleteMarkedDayByDate(date: String) = dao.deleteMarkedDayByDate(date)

}