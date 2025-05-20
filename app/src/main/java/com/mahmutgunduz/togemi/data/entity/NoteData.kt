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
    var noteText: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
