package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.data.dao.NoteDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FavoriteNotesViewModel(
    private val noteDao: NoteDao
) : ViewModel() {

    private val _note = MutableLiveData<NoteData>()
    val note: LiveData<NoteData> = _note

    private val compositeDisposable = CompositeDisposable()
    private val _noteList = MutableLiveData<List<NoteData>>()
    val noteList: LiveData<List<NoteData>> = _noteList

    fun favoriteNotes() {
        compositeDisposable.add(
            noteDao.getFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ notes ->
                    _noteList.value = notes
                }, { error ->
                    // Hata durumunda boş liste göster
                    _noteList.value = emptyList()
                    error.printStackTrace()
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

