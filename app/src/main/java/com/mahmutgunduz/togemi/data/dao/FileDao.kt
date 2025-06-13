package com.mahmutgunduz.togemi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mahmutgunduz.togemi.data.entity.FileData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


@Dao
interface FileDao {

    @Insert
    fun insert(note: FileData) : Completable
    @Update
    fun update(note: FileData): Completable
    @Delete
    fun delete(note: FileData): Completable
    @Query("SELECT * FROM FileData")
    fun getAllNotes(): Single<List<FileData>>

    @Query("SELECT * FROM FileData WHERE id = :noteId")
    fun getFileById(noteId: Int): Single<FileData>
}