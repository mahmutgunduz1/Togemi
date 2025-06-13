package com.mahmutgunduz.togemi.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "todo_items")
class ToDoData(


    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,


    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


}