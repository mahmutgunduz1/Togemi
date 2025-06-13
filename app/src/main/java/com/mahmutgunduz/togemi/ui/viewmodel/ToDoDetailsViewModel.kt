package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmutgunduz.togemi.data.dao.ToDoDao
import com.mahmutgunduz.togemi.data.entity.FileData
import com.mahmutgunduz.togemi.data.entity.ToDoData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ToDoDetailsViewModel(
    private val toDoDao: ToDoDao
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _noteList = MutableLiveData<List<ToDoData>>()
    val noteList: LiveData<List<ToDoData>>
        get() = _noteList



    fun toDoInsert(toDoData: ToDoData) {
        compositeDisposable.add(
            toDoDao.insert(
                toDoData
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, { error ->


                })
        )

    }
}