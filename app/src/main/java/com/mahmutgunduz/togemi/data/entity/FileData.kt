package com.mahmutgunduz.togemi.data.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FileData")
data class FileData(
    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "type")
    val type: String?,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "size")
    val size: Long?,

    @ColumnInfo(name = "uri")
    val uri: Uri?,
    @ColumnInfo(name = "noteId")
    val noteId: Int

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}