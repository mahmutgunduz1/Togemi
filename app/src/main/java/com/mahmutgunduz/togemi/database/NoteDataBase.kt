package com.mahmutgunduz.togemi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mahmutgunduz.togemi.data.entity.NoteData

@Database(entities = [NoteData::class], version = 1, exportSchema = false)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}