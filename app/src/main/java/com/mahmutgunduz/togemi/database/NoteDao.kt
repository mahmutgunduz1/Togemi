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

    @Query("SELECT * FROM NoteTable WHERE title LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Single<List<NoteData>>

    @Query("SELECT * FROM NoteTable WHERE password = :password")
    fun getPassword(password: String): Single<NoteData>

    @Query("UPDATE NoteTable SET password = :password, isLocked = 1 WHERE id = :noteid")
    fun setPassword(noteid: Int, password: String): Completable

    @Query("UPDATE NoteTable SET isLocked = :isLocked WHERE id = :noteId")
    fun updateLockStatus(noteId: Int, isLocked: Boolean): Completable

    @Query("SELECT * FROM NoteTable WHERE isFavorite = 1")
    fun getFavorite(): Single<List<NoteData>>








}