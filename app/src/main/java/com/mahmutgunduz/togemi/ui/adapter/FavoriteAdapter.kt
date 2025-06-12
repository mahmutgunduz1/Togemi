package com.mahmutgunduz.togemi.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.ItemFavoriteNoteBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FavoriteAdapter(
    private var favoriteList: List<NoteData>,
    private val onItemClick: (NoteData) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    fun updateList(newList: List<NoteData>) {
        favoriteList = newList
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(val binding: ItemFavoriteNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteData) {
            binding.apply {
                noteTitleTextView.text = note.title
                dateTextView.text = getFormattedDate(note.date)
                
                // Kart tıklama
                root.setOnClickListener { onItemClick(note) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteList[position])
    }

    override fun getItemCount() = favoriteList.size

    private fun getFormattedDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr"))
        return sdf.format(timeInMillis)
    }
}