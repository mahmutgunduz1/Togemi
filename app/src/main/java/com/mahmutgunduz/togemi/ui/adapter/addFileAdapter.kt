package com.mahmutgunduz.togemi.ui.adapter


import androidx.core.net.toUri
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahmutgunduz.togemi.data.entity.FileData
import com.mahmutgunduz.togemi.databinding.ItemAttachmentBinding

class addFileAdapter(
    private val fileList: List<FileData>,
    private val onDeleteClick: (FileData) -> Unit
) :
    RecyclerView.Adapter<addFileAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemAttachmentBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val file = fileList[position]

        holder.binding.tvAttachmentName.text = file.title
        holder.binding.tvFileSize.text = file.type

        Glide.with(holder.itemView.context)
            .load(file.uri)
            .into(holder.binding.ivAttachment)

        holder.binding.ivOptions.setOnClickListener {


            onDeleteClick(file)
        }

        holder.binding.ivAttachment.setOnClickListener {
            val context = holder.itemView.context
            val uri = file.uri ?: return@setOnClickListener
            val intent = Intent(Intent.ACTION_VIEW)

            // MIME type belirle (örnek: PDF, resim vs.)
            val type = context.contentResolver.getType(uri) ?: "*/*"

            intent.setDataAndType(uri, type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Intent'i başlat
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Dosya açılamıyor", Toast.LENGTH_SHORT).show()
            }
        }
    }


}