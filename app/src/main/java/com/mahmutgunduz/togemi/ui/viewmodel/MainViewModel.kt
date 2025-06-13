package com.mahmutgunduz.togemi.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.data.dao.NoteDao

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers



class MainViewModel(
    private val noteDao: NoteDao
) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()
    private val _noteList = MutableLiveData<List<NoteData>>()
    val noteList: LiveData<List<NoteData>> = _noteList


    init {
        loadNotes()

    }

     fun loadNotes() {
        compositeDisposable.add(
            noteDao.getAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    _noteList.value = notes
                }
        )
    }

    fun addNote(note: NoteData) {
        compositeDisposable.add(
            noteDao.insert(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loadNotes()
                }
        )
    }

    fun deleteNote(note: NoteData) {
        compositeDisposable.add(
            noteDao.delete(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loadNotes()
                }
        )
    }




    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


    fun setPassword(noteId: String, password: String) {
        compositeDisposable.add(
            noteDao.setPassword(noteId.toInt(), password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loadNotes()
                }, { error ->
                    error.printStackTrace()
                })
        )
    }


    fun searchNotes(query: String){

        compositeDisposable.add(
            noteDao.searchNotes(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->

                    _noteList.value = notes
                }
        )
    }


}
