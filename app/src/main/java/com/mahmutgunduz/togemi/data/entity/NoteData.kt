package com.mahmutgunduz.togemi.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "NoteTable")
class NoteData(


    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "noteText")
    var noteText: String,
    @ColumnInfo(name = "date")
    var date: Long= System.currentTimeMillis(),
    @ColumnInfo(name = "password")
    var password: String? = null,
    @ColumnInfo(name = "isLocked")
    var isLocked: Boolean = false,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false


) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
