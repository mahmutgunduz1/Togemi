package com.mahmutgunduz.togemi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData

import com.mahmutgunduz.togemi.databinding.RecyclerRowBinding
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.service.alertDialogTrueFalse
import com.mahmutgunduz.togemi.service.alertPassword
import java.text.SimpleDateFormat
import java.util.Locale

class NoteAdapter(
    private var noteList: MutableList<NoteData>,
    val viewModel: MainViewModel,
    val context: Context,
    private val onItemClick: (NoteData) -> Unit


    ) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

    class NoteViewHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.binding.apply {
            titleTextView.text = note.title
            dateTextView.text = formatDate(note.date)

            if (note.isLocked) {
                // Şifreli not görünümü
                noteTextView.visibility = View.GONE
                lockedContentLayout.visibility = View.VISIBLE
                lockImage.visibility = View.VISIBLE

                root.setOnClickListener {
                    context.alertPassword(
                        layoutInflater = LayoutInflater.from(context),
                        viewModelMain = viewModel,
                        noteId = note.id,
                        onPasswordCorrect = {
                            onItemClick( note)



                        }
                    )
                }
            } else {
                // Normal not görünümü
                noteTextView.visibility = View.VISIBLE
                noteTextView.text = note.noteText
                lockedContentLayout.visibility = View.GONE
                lockImage.visibility = View.GONE

                root.setOnClickListener {

                    onItemClick(note)



                }
            }

            moreImage.setOnClickListener {
                showPopupMenu(it, note, holder.binding)
            }
        }
    }

    private fun showPopupMenu(view: View, note: NoteData, binding: RecyclerRowBinding) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.note_options_menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.noteDelete -> {
                    context.alertDialogTrueFalse(
                        title = "Notu Sil",
                        message = "Notu silmek istediğinize emin misiniz?",
                        positiveButtonText = "Evet",
                        negativeButtonText = "Hayır",
                        onPositiveClick = {
                            if (note.isLocked) {

                                Snackbar.make(
                                    view,
                                    "Şifreli notu silmek için şifre girmeniz gerekmektedir.",
                                    Snackbar.LENGTH_INDEFINITE
                                ).setAction("Tamam", View.OnClickListener {

                                    context.alertPassword(
                                        layoutInflater = LayoutInflater.from(context),
                                        viewModelMain = viewModel,
                                        noteId = note.id,
                                        onPasswordCorrect = {
                                            viewModel.deleteNote(note)
                                        })

                                }).show()


                            } else {
                                viewModel.deleteNote(note)
                            }

                        },
                        mainViewModel = viewModel
                    )

                    true
                }

                R.id.noteLock -> {
                    if (!note.isLocked) {
                        context.alertPassword(
                            layoutInflater = LayoutInflater.from(context),
                            viewModelMain = viewModel,
                            noteId = note.id,
                            onPasswordCorrect = {
                                note.isLocked = true
                                updateNoteVisibility(note, binding)
                                Toast.makeText(context, "Not şifrelendi", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Not zaten şifreli", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun updateNoteVisibility(note: NoteData, binding: RecyclerRowBinding) {
        if (note.isLocked) {
            binding.apply {
                noteTextView.visibility = View.GONE
                lockedContentLayout.visibility = View.VISIBLE
                lockImage.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                noteTextView.visibility = View.VISIBLE
                noteTextView.text = note.noteText
                lockedContentLayout.visibility = View.GONE
                lockImage.visibility = View.GONE
            }
        }
    }

    fun updateData(newData: List<NoteData>) {
        noteList.clear()
        noteList.addAll(newData)
        notifyDataSetChanged()
    }

}