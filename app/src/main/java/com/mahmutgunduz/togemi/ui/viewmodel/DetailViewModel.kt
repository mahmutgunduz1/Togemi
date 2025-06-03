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


class DetailViewModel(
    private val noteDao: NoteDao
): ViewModel() {

    private val _note = MutableLiveData<NoteData>()
    val note: LiveData<NoteData> = _note


    private val compositeDisposable = CompositeDisposable()
    private val _noteList = MutableLiveData<List<NoteData>>()
    val noteList: LiveData<List<NoteData>> = _noteList

    // Silme işlemi sonucu için LiveData
    private val _deleteResult = MutableLiveData<DeleteResult>()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    // Silme işlemi sonucu için sealed class
    sealed class DeleteResult {
        object Success : DeleteResult()
        data class Error(val message: String) : DeleteResult()
    }



    fun setNote(note: NoteData) {
        _note.value = note
    }



    fun updateNote(note: NoteData) {

        compositeDisposable.add(
            noteDao.update(note)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // update  başarılı

                }, { error ->
                    // update başarısız

                })
        )

    }

    fun deleteNote(note: NoteData) {
        compositeDisposable.add(
            noteDao.delete(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // Silme başarılı
                    _deleteResult.value = DeleteResult.Success
                }, { error ->
                    // Silme başarısız
                    _deleteResult.value = DeleteResult.Error("Not silinirken hata oluştu: ${error.message}")
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }



}