package com.mahmutgunduz.togemi.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
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
        mainViewModel = ViewModelProvider(requireActivity(), mainFactory).get(MainViewModel::class.java)
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
                    Toast.makeText(requireContext(), "Not başarıyla silindi", Toast.LENGTH_SHORT).show()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
