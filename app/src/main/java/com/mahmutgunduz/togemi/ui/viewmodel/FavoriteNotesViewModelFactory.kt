package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahmutgunduz.togemi.database.NoteDao

class FavoriteNotesViewModelFactory(
    private val noteDao: NoteDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteNotesViewModel::class.java)) {
            return FavoriteNotesViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}