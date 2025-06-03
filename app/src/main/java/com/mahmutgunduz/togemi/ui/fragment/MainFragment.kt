package com.mahmutgunduz.togemi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.database.NoteDataBase
import com.mahmutgunduz.togemi.databinding.FragmentMainBinding
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.ui.adapter.NoteAdapter
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModelFactory
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModelFactory
import com.mahmutgunduz.togemi.utils.alertDialog


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private var _binding: FragmentMainBinding? = null
    private lateinit var adapter: NoteAdapter
    private lateinit var noteDataBase: NoteDataBase
    private lateinit var noteDao: com.mahmutgunduz.togemi.database.NoteDao
    private lateinit var noteList: ArrayList<NoteData>
    private lateinit var viewModelMain: MainViewModel
    private lateinit var viewModelDetail: DetailViewModel


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

        noteDataBase = Room.databaseBuilder(
            requireContext(),
            NoteDataBase::class.java,
            "note_database"
        ).build()

        noteDao = noteDataBase.noteDao()

        // ViewModelFactory kullanarak ViewModel oluştur
        val mainFactory = MainViewModelFactory(noteDao)
        viewModelMain = ViewModelProvider(this, mainFactory)[MainViewModel::class.java]
        
        // DetailViewModel'i başlat
        val detailFactory = DetailViewModelFactory(noteDao)
        viewModelDetail = ViewModelProvider(this, detailFactory)[DetailViewModel::class.java]
        
        // Test verilerini ViewModel üzerinden ekle

        val prefs = requireContext().getSharedPreferences("app_prefs", 0)
        val isFirstRun = prefs.getBoolean("first_run", true)

        if (isFirstRun) {
            // Sadece ilk çalıştırmada eklenir
            viewModelMain.addNote(NoteData("test1", "Horton kimi duyuyor..."))
            prefs.edit().putBoolean("first_run", false).apply()
        }






        binding.fabAddTask.setOnClickListener {
            val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)

            requireContext().alertDialog(
                dialogBinding.etSubject.text.toString(),
                dialogBinding.etNote.text.toString(),
                layoutInflater,
                viewModelMain,
                requireContext()
                , viewModelDetail
            )
            Toast.makeText(requireContext(), "Not eklendi", Toast.LENGTH_SHORT).show()


        }

        observeViewModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(emptyList(), viewModelMain, requireContext())
        binding.rvTasks.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@MainFragment.adapter

        }
    }

    private fun observeViewModel() {
        viewModelMain.noteList.observe(viewLifecycleOwner) { notes ->
            adapter = NoteAdapter(notes, viewModelMain, requireContext())
            binding.rvTasks.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


