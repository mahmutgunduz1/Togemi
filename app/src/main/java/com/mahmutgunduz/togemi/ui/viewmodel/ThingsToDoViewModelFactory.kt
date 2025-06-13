package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahmutgunduz.togemi.data.dao.ToDoDao

class ThingsToDoViewModelFactory(
    private val toDoDao: ToDoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThingsToDoViewModel::class.java)) {
            return ThingsToDoViewModel(toDoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

