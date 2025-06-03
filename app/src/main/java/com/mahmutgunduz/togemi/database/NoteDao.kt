package com.mahmutgunduz.togemi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mahmutgunduz.togemi.data.entity.NoteData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface NoteDao {

    @Insert
    fun insert(note: NoteData) : Completable
    @Update
    fun update(note: NoteData): Completable
    @Delete
    fun delete(note: NoteData): Completable
    @Query("SELECT * FROM NoteTable")
    fun getAllNotes(): Single<List<NoteData>>

    @Query("SELECT * FROM NoteTable WHERE id = :noteId")
    fun getNoteById(noteId: Int): Single<NoteData>



}