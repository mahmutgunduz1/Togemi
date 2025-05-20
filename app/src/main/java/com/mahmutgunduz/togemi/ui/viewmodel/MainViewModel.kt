package com.mahmutgunduz.togemi.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.database.NoteDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel (private val noteDao :NoteDao): ViewModel() {
    private var compositeDisposable = CompositeDisposable()





    private val _noteList = MutableLiveData<List<NoteData>>()
    val noteList: LiveData<List<NoteData>> = _noteList

    init {
        _noteList.value = mutableListOf()
    }

    fun deleteNote(note: NoteData) {
        val currentList = _noteList.value?.toMutableList() ?: mutableListOf()
        currentList.remove(note)
        _noteList.value = currentList
    }

    fun addNote(note: NoteData) {
        val currentList = _noteList.value?.toMutableList() ?: mutableListOf()
        currentList.add(note)
        _noteList.value = currentList
    }

    fun SaveToRoom(note: NoteData){
        compositeDisposable.add(
            noteDao.insert(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(){
                    println("Başarılı")
                }


        )
    }
    }
