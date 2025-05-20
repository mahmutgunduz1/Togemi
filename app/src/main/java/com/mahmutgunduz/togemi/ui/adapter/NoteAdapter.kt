package com.mahmutgunduz.togemi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.RecyclerRowBinding
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel

class NoteAdapter(
    val noteList: List<NoteData>,
    val viewModel: MainViewModel

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
        holder.binding.moreImage.setOnClickListener {
            viewModel.deleteNote(note)
        }



    }
}