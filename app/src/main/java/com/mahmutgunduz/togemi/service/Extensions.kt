package com.mahmutgunduz.togemi.service

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.databinding.PasswordAlertDialogBinding
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel


fun Context.alertDialog(
    title: String,
    message: String, layoutInflater: LayoutInflater, viewModelMain: MainViewModel,
    context: Context,
    viewModelDetail: DetailViewModel


) {


    val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setView(dialogBinding.root)
        .create()

    dialogBinding.btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    dialogBinding.btnSave.setOnClickListener {

        val subject = dialogBinding.etSubject.text.toString()
        val note = dialogBinding.etNote.text.toString()

        if (subject.isNotEmpty() && note.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            val newNote = NoteData(subject, note, currentTime)
            viewModelMain.addNote(newNote)

            Toast.makeText(context, " Not eklendi ", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        } else {

            Toast.makeText(this, "Alanlar boş bırakılamaz", Toast.LENGTH_SHORT).show()
        }
    }
    dialog.show()
}


fun Context.alertDialogTrueFalse(
    title: String,
    message: String,
    iconResId: Int? = null,
    positiveButtonText: String = "Evet",
    negativeButtonText: String = "Hayır",
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit = {},
    mainViewModel: MainViewModel? = null
) {
    // Dialog oluşturma
    val builder = AlertDialog.Builder(this)
    val inflater = LayoutInflater.from(this)
    val dialogView = inflater.inflate(R.layout.custom_alert_dialog, null)
    val binding = com.mahmutgunduz.togemi.databinding.CustomAlertDialogBinding.bind(dialogView)


    binding.tvDialogTitle.text = title
    binding.tvDialogMessage.text = message


    binding.btnPositive.text = positiveButtonText
    binding.btnNegative.text = negativeButtonText


    val dialog = builder.setView(dialogView).create()

    // Arka planı transparan yaparak custom görünüm sağlama*************
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    // Animasyon ekleme
    binding.dialogCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.dialog_fade_in))


    binding.btnPositive.setOnClickListener {
        dialog.dismiss()
        onPositiveClick()
    }
    binding.btnNegative.setOnClickListener {
        dialog.dismiss()
        onNegativeClick()
    }

    dialog.show()
}


fun Context.alertPassword(
    layoutInflater: LayoutInflater,
    viewModelMain: MainViewModel,
    noteId: Int,
    onPasswordCorrect: () -> Unit = {},

) {
    val builder = AlertDialog.Builder(this)
    val dialogBinding = PasswordAlertDialogBinding.inflate(layoutInflater)
    builder.setView(dialogBinding.root)
    val dialog = builder.create()

    dialogBinding.btnSave.setOnClickListener {
        val password = dialogBinding.etPassword.text.toString()
        val passwordConfirm = dialogBinding.etPasswordConfirm.text.toString()


        if (password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Şifre boş bırakılamaz", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }   else{

            if (password != passwordConfirm) {
                Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }


        viewModelMain.setPassword(noteId.toString(), password)

        onPasswordCorrect()
        dialog.dismiss()
    }

    dialogBinding.btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}

