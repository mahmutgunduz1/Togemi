package com.mahmutgunduz.togemi.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel

fun Context.alertDialog(  title: String,
                  message: String,layoutInflater : LayoutInflater, viewModel: MainViewModel, context: Context){


    val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder( context)
        .setView(dialogBinding.root)
        .create()

    dialogBinding.btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    dialogBinding.btnSave.setOnClickListener {
        val subject = dialogBinding.etSubject.text.toString()
        val note = dialogBinding.etNote.text.toString()

        if (subject.isNotEmpty() && note.isNotEmpty()) {
            val newNote = NoteData(0, subject, note)
            viewModel.addNote(newNote)
            Toast.makeText( context," Not eklendi ", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
    dialog.show()
}