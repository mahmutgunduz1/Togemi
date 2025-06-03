package com.mahmutgunduz.togemi.ui.viewmodel


import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.database.NoteDao

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


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

    fun intentToDetail(note: NoteData, view: android.view.View) {
        val bundle = Bundle()
        bundle.putSerializable("note", note)
        bundle.putInt("id", note.id)
        bundle.putString("title", note.title)
        bundle.putString("noteText", note.noteText)
        Navigation.findNavController(view)
            .navigate(R.id.action_mainFragment_to_detailFragment, bundle)


    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
