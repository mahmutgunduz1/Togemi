package com.mahmutgunduz.togemi.utils
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
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
            val newNote = NoteData(subject, note)
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
    binding. dialogCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.dialog_fade_in))


    binding.btnPositive.setOnClickListener {
        dialog.dismiss()
        onPositiveClick()
    }
    binding. btnNegative.setOnClickListener {
        dialog.dismiss()
        onNegativeClick()
    }

    dialog.show()
}