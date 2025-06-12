package com.mahmutgunduz.togemi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mahmutgunduz.togemi.data.entity.FileData
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.service.Converters

@Database(entities = [NoteData::class, FileData::class], version = 8, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    abstract fun fileDao(): FileDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDataBase? = null

        fun getInstance(context: Context): NoteDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDataBase::class.java,
                    "note_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}