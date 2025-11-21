package com.uvg.rachapp.data.local.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uvg.rachapp.data.local.db.daos.NoteDao
import com.uvg.rachapp.data.local.db.entities.NoteEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [NoteEntity::class], version = 1)
abstract class JournalDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: JournalDatabase? = null

        fun getDatabase(context: Context): JournalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object JournalDatabaseModule {
    @Provides
    @Singleton
    fun provideJournalDatabase(@ApplicationContext context: Context): JournalDatabase {
        return Room.databaseBuilder(
            context,
            JournalDatabase::class.java,
            "journal_db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: JournalDatabase): NoteDao {
        return database.noteDao()
    }

}