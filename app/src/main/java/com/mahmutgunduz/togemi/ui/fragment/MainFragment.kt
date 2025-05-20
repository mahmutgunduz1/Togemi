package com.mahmutgunduz.togemi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.database.NoteDataBase
import com.mahmutgunduz.togemi.databinding.FragmentMainBinding
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.ui.adapter.NoteAdapter
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.utils.alertDialog


class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private var _binding: FragmentMainBinding? = null
    private lateinit var adapter: NoteAdapter
    private lateinit var noteList: ArrayList<NoteData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Test verilerini ViewModel üzerinden ekle
        viewModel.addNote(NoteData( "test1", "Horton kimi duyuyor nŞimşek mcquenn Kayıp balık memo"))
        viewModel.addNote(NoteData( "test2", "Horton kimi duyuyor Şimşek mcquenn KayıpRafmemo"))
        viewModel.addNote(NoteData("test3", "Horton kimi duyuyor Şimşek mcquenn KayıpRafmemo"))
        viewModel.addNote(NoteData( "test4", "Horton kimi duyuyor Şimşek mcquenn  KayıpRafmemo"))
        viewModel.addNote(NoteData( "test5", "Horton kimi duyuyor Şimşek mcquenn KayıpRafmemo"))
        viewModel.addNote(NoteData("test6", "Horton kimi duyuyor Şimşek mcquenn KayıpRafmemo"))
        viewModel.addNote(NoteData( "test7", "Horton kimi duyuyor Şimşek mcquenn KayıpRafmemo"))



        binding.fabAddTask.setOnClickListener {
            val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)

     requireContext().alertDialog(dialogBinding.etSubject.text.toString(),dialogBinding.etNote.text.toString(),layoutInflater,viewModel,requireContext())
        }

        observeViewModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(emptyList(), viewModel)
        binding.rvTasks.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = this@MainFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.noteList.observe(viewLifecycleOwner) { notes ->
            adapter = NoteAdapter(notes, viewModel)
            binding.rvTasks.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    }


