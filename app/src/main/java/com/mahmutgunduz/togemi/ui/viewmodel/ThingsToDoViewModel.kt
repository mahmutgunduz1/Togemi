package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mahmutgunduz.togemi.data.dao.ToDoDao
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.data.entity.ToDoData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ThingsToDoViewModel(
    private val toDoDao: ToDoDao
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _noteList = MutableLiveData<List<NoteData>>()





}