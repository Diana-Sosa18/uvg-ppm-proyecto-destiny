package com.uvg.rachapp.data.local.db

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uvg.rachapp.data.local.db.daos.MarkedDayDao
import com.uvg.rachapp.data.local.db.daos.NoteDao
import com.uvg.rachapp.data.local.db.entities.StreakDayEntity
import com.uvg.rachapp.data.local.db.entities.NoteEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Database(entities = [StreakDayEntity::class, NoteEntity::class], version = 2)
abstract class InAppDatabase : RoomDatabase() {
    abstract fun markedDayDao(): MarkedDayDao
    abstract fun noteDao(): NoteDao

    companion object{
        @Volatile
        private var INSTANCE: InAppDatabase? = null

        fun getDatabase(context: Context): InAppDatabase {
            return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        InAppDatabase::class.java,
                        "streak_db"
                    ).fallbackToDestructiveMigration()
                        .build().also { INSTANCE = it }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InAppDatabase {
        return Room.databaseBuilder(
            context,
            InAppDatabase::class.java,
            "streak_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideMarkedDayDao(database: InAppDatabase): MarkedDayDao {
        return database.markedDayDao()


    }

    @Provides
    fun provideNoteDao(database: InAppDatabase): NoteDao {
        return database.noteDao()
    }
}

