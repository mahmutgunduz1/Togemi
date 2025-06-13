package com.mahmutgunduz.togemi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mahmutgunduz.togemi.data.entity.FileData
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.data.entity.ToDoData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single


@Dao
interface ToDoDao {

    @Insert
    fun insert(note: ToDoData) : Completable




     @Query("SELECT * FROM todo_items")
     fun getAllToDoItems() : Single<List<ToDoData>>

    @Query("SELECT * FROM todo_items WHERE id = :toDoId")
    fun getNoteById(toDoId: Int): Single<ToDoData>






}