package com.uvg.rachapp.data.local.db

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uvg.rachapp.data.local.db.daos.MarkedDayDao
import com.uvg.rachapp.data.local.db.entities.StreakDayEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Database(entities = [StreakDayEntity::class], version = 1)
abstract class InAppDatabase : RoomDatabase() {
    abstract fun markedDayDao(): MarkedDayDao

    companion object{
        @Volatile
        private var INSTANCE: InAppDatabase? = null

        fun getDatabase(context: Context): InAppDatabase {
            return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        InAppDatabase::class.java,
                        "streak_db"
                    ).build().also { INSTANCE = it }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Aquí es donde ustedes también pueden proporcionar las instancias de la base de datos y DAOs (gracias a Hilt para inyección de dependencias)
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InAppDatabase {
        return Room.databaseBuilder(
            context,
            InAppDatabase::class.java,
            "streak_db"
        ).build()
    }

    @Provides
    fun provideMarkedDayDao(database: InAppDatabase): MarkedDayDao {
        return database.markedDayDao()


    }
}

