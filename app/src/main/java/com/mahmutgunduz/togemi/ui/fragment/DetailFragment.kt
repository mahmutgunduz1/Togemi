package com.mahmutgunduz.togemi.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.databinding.FilepickerBottomSheetBinding
import com.mahmutgunduz.togemi.databinding.FragmentDetailBinding
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModelFactory
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModelFactory
import com.mahmutgunduz.togemi.utils.alertDialog
import com.mahmutgunduz.togemi.utils.alertDialogTrueFalse

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteDataBase: com.mahmutgunduz.togemi.database.NoteDataBase
    private lateinit var noteData: NoteData
    private lateinit var noteDao: com.mahmutgunduz.togemi.database.NoteDao
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteDataBase = com.mahmutgunduz.togemi.database.NoteDataBase.getInstance(requireContext())
        noteDao = noteDataBase.noteDao()

        val detailFactory = DetailViewModelFactory(noteDao)
        detailViewModel = ViewModelProvider(this, detailFactory).get(DetailViewModel::class.java)

        // MainViewModel'i başlat
        val mainFactory = MainViewModelFactory(noteDao)
        mainViewModel =
            ViewModelProvider(requireActivity(), mainFactory).get(MainViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val id = arguments?.getInt("id") ?: 0
        val title = arguments?.getString("title") ?: ""
        val noteText = arguments?.getString("noteText") ?: ""

        // noteData nesnesi oluşturuluyor
        noteData = NoteData(title = title, noteText = noteText).apply {
            this.id = id
        }

        detailViewModel.setNote(noteData)


        detailViewModel.note.observe(viewLifecycleOwner) { note ->
            binding.textTitle.text = note.title
            binding.textContent.text = note.noteText
        }


        binding.btnDelete.setOnClickListener {

            requireContext().alertDialogTrueFalse(
                title = "Notu Sil",
                message = "Notu silmek istediğinize emin misiniz?",
                positiveButtonText = "Evet",
                negativeButtonText = "Hayır",
                onPositiveClick = {
                    detailViewModel.deleteNote(noteData)
                }
            )

        }

        // Silme sonucunu gözle
        detailViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DetailViewModel.DeleteResult.Success -> {
                    Toast.makeText(requireContext(), "Not başarıyla silindi", Toast.LENGTH_SHORT)
                        .show()

                    val bundle = Bundle().apply {
                        putBoolean("noteDeleted", true)
                        putInt("deletedNoteId", noteData.id)
                    }

                    findNavController().navigate(R.id.action_detailFragment_to_mainFragment, bundle)
                }

                is DetailViewModel.DeleteResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnShare.setOnClickListener {
            shareNote(noteData)
        }


        readMode()

        backButton()


        binding.btnEdit.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)
            builder.setView(dialogBinding.root)

            dialogBinding.etSubject.setText(noteData.title)
            dialogBinding.etNote.setText(noteData.noteText)
            dialogBinding.tvTitle.text = "Notu Güncelle"
            val dialog = builder.create()
            dialog.show()

            dialogBinding.btnSave.setOnClickListener {

                val subject = dialogBinding.etSubject.text.toString()
                val note = dialogBinding.etNote.text.toString()

                if (subject.isNotEmpty() && note.isNotEmpty()) {
                    val updatedNote = NoteData(subject, note).apply {
                        this.id = noteData.id
                    }
                    detailViewModel.updateNote(updatedNote)
                    Toast.makeText(
                        requireContext(),
                        "Not başarıyla güncellendi",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.textTitle.text = updatedNote.title
                    binding.textContent.text = updatedNote.noteText

                    dialog.dismiss()
                } else {

                    Toast.makeText(requireContext(), "Alanlar boş olamaz", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.btnAddAttachment.setOnClickListener {



            showFilePickerBottomSheet()







        }
    }

    val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleSelectedFile(it)
        }
    }


    private fun handleSelectedFile(uri: Uri) {
        val mimeType = requireContext().contentResolver.getType(uri)

        Log.d("SelectedFile", "Uri: $uri, MIME Type: $mimeType")
        // Örneğin RecyclerView'e ekle, sunucuya yükle vs.
        // Buraya kendi işlem mantığını koyabilirsin.
    }

    private fun showFilePickerBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val binding = FilepickerBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.ivImage.setOnClickListener {
            pickFileLauncher.launch("image/*")
            dialog.dismiss()
        }

        binding.ivVideo.setOnClickListener {
            pickFileLauncher.launch("video/*")
            dialog.dismiss()
        }

        binding.ivAudio.setOnClickListener {
            pickFileLauncher.launch("audio/*")
            dialog.dismiss()
        }

        binding.ivDocument.setOnClickListener {
            pickFileLauncher.launch("*/*")
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun backButton() {

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun readMode() {
        binding.chipReadMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.textTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                binding.chipGroup.visibility = View.GONE
                binding.tvAttachmentsTitle.visibility = View.GONE
                binding.recyclerAttachments.visibility = View.GONE
                binding.tvEmptyAttachments.visibility = View.GONE
                binding.cardAttachments.visibility = View.GONE
                binding.tvModesTitle.visibility = View.GONE
                binding.chipNightMode.visibility = View.GONE
                binding.chipFavorite.visibility = View.GONE
                binding.tvInfo.visibility = View.GONE
                binding.ivPinned.visibility = View.GONE
                binding.btnReminder.visibility = View.GONE

                binding.bottomAppBar.visibility = View.GONE

            } else {
                binding.chipGroup.visibility = View.VISIBLE
                binding.tvAttachmentsTitle.visibility = View.VISIBLE
                binding.recyclerAttachments.visibility = View.VISIBLE
                binding.tvEmptyAttachments.visibility = View.VISIBLE
                binding.cardAttachments.visibility = View.VISIBLE
                binding.tvModesTitle.visibility = View.VISIBLE
                binding.chipNightMode.visibility = View.VISIBLE
                binding.chipFavorite.visibility = View.VISIBLE
                binding.tvInfo.visibility = View.VISIBLE
                binding.ivPinned.visibility = View.VISIBLE
                binding.btnReminder.visibility = View.VISIBLE
                binding.bottomAppBar.visibility = View.VISIBLE

            }
        }
    }


    private fun shareNote(note: NoteData) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Başlık: ${note.title}\n\nİçerik: ${note.noteText}")
            type = "text/plain"
        }
        // Uygulama seçici çıkart (kullanıcı hangi uygulamayla paylaşacaksa onu seçebilir)
        val chooser = Intent.createChooser(shareIntent, "Notu paylaş")
        if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(requireContext(), "Paylaşacak uygulama bulunamadı", Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
