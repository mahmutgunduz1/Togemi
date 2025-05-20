package com.mahmutgunduz.togemi.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mahmutgunduz.togemi.data.entity.NoteData

interface NoteDao {

    @Insert
    fun insert(note: NoteData)
    @Update
    fun update(note: NoteData)
    @Delete
    fun delete(note: NoteData)
    @Query("SELECT * FROM NoteTable")
    fun getAllNotes(): List<NoteData>

    @Query("SELECT * FROM NoteTable WHERE id = :noteId")
    fun getNoteById(noteId: Int): NoteData?



}