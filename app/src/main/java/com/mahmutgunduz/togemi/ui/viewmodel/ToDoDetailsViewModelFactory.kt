package com.mahmutgunduz.togemi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahmutgunduz.togemi.data.dao.ToDoDao

class ToDoDetailsViewModelFactory (
    private val toDoDao: ToDoDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoDetailsViewModel::class.java)) {
            return ToDoDetailsViewModel(toDoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}