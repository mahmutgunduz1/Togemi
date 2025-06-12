package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahmutgunduz.togemi.database.FileDao
import com.mahmutgunduz.togemi.database.NoteDao

class DetailViewModelFactory(
    private val noteDao: NoteDao,
    private val fileDao: FileDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {


            return DetailViewModel(noteDao, fileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}