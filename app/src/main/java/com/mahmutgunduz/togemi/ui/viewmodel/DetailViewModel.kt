package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmutgunduz.togemi.data.entity.FileData
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.database.FileDao
import com.mahmutgunduz.togemi.database.NoteDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch


class DetailViewModel(
    private val noteDao: NoteDao,
    private val fileDao: FileDao
) : ViewModel() {

    private val _note = MutableLiveData<NoteData>()
    val note: LiveData<NoteData> = _note


    private val compositeDisposable = CompositeDisposable()
    private val _noteList = MutableLiveData<List<NoteData>>()
    val noteList: LiveData<List<NoteData>> = _noteList
    private val _fileList = MutableLiveData<List<FileData>>()
    val fileList: LiveData<List<FileData>> = _fileList

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

    fun getFilesForNote(noteId: Int): LiveData<List<FileData>> {
        val fileListData = MutableLiveData<List<FileData>>()
        compositeDisposable.add(
            fileDao.getAllNotes()
                .map { files -> files.filter { it.noteId == noteId } }  // Filter files by noteId
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ files ->
                    // Tüm dosyaları aldıktan sonra noteId ile ilgili olanları filtreleyebiliriz
                    // Bu örnekte tüm dosyaları döndürüyoruz, gerçek uygulamada filtreleme yapılmalı
                    fileListData.postValue(files)
                }, { error ->
                    // Hata durumunda boş liste döndür
                    fileListData.postValue(emptyList())
                })
        )
        return fileListData
    }


    fun fileDelete(fileData: FileData) {
        compositeDisposable.add(
            fileDao.delete(fileData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // silme başarılı
                }, { error ->
                    // hata yönetimi
                })
        )
    }


    fun fileInsert(fileData: FileData) {
        compositeDisposable.add(
            fileDao.insert(
                fileData
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // update  başarılı

                }, { error ->
                    // update başarısız

                })
        )

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
                    _deleteResult.value =
                        DeleteResult.Error("Not silinirken hata oluştu: ${error.message}")
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}