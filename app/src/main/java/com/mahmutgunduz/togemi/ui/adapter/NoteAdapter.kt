package com.mahmutgunduz.togemi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.RecyclerRowBinding
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.utils.alertDialogTrueFalse

class NoteAdapter(
    val noteList: List<NoteData>,
    val viewModel: MainViewModel,
    val context: Context
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
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
        holder.binding.titleTextView.text = note.title
        holder.binding.noteTextView.text = note.noteText


        holder.binding.root.setOnClickListener {
            viewModel.intentToDetail(note, it)
        }
        holder.binding.moreImage.setOnClickListener {
                    context.alertDialogTrueFalse(
                        title = "Notu Sil",
                        message = "Notu silmek istediğinize emin misiniz?",
                        positiveButtonText = "Evet",
                        negativeButtonText = "Hayır",
                        onPositiveClick = {
                            viewModel.deleteNote(note)
                        },
                        mainViewModel = viewModel

                    )

        }



    }
}