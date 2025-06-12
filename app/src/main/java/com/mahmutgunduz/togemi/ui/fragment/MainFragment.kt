package com.mahmutgunduz.togemi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.database.NoteDataBase
import com.mahmutgunduz.togemi.databinding.FragmentMainBinding
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.ui.adapter.NoteAdapter
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModelFactory
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModelFactory
import com.mahmutgunduz.togemi.service.alertDialog


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

        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteDataBase = Room.databaseBuilder(
            requireContext(),
            NoteDataBase::class.java,
            "note_database"
        ).fallbackToDestructiveMigration()
            .build()

        noteDao = noteDataBase.noteDao()

        // ViewModelFactory kullanarak ViewModel oluştur
        val mainFactory = MainViewModelFactory(noteDao)
        viewModelMain = ViewModelProvider(this, mainFactory)[MainViewModel::class.java]

        // DetailViewModel'i başlat
        val detailFactory = DetailViewModelFactory(noteDao, noteDataBase.fileDao())
        viewModelDetail = ViewModelProvider(this, detailFactory)[DetailViewModel::class.java]





        fabButtonClick()
        observeViewModel()


        setupSearchView()
        setupRecyclerView()

    }

    private fun fabButtonClick() {
        binding.fabAddTask.setOnClickListener {
            val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)

            requireContext().alertDialog(
                dialogBinding.etSubject.text.toString(),
                dialogBinding.etNote.text.toString(),
                layoutInflater,
                viewModelMain,
                requireContext(), viewModelDetail
            )
            Toast.makeText(requireContext(), "Not eklendi", Toast.LENGTH_SHORT).show()


        }

    }


    //Kotlinde birden fazla ayar yapcağın zaman .apply kullanıyorsun.
    private fun setupRecyclerView() {
        adapter = NoteAdapter(mutableListOf(), viewModelMain, requireContext(), onItemClick = {


            val bundle = Bundle().apply {
                putInt("id", it.id)
                putString("title", it.title)
                putString("noteText", it.noteText)
            }

            findNavController().navigate(
                R.id.action_mainFragment_to_detailFragment,
                bundle
            )


        })

        binding.rvTasks.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@MainFragment.adapter

        }
    }


    // observe() = Bu veriyi izle, değişirse bana haber ver.
    // Güncellenen data ile UI'yı güncelle viewLifecycleOwner-> this gibi bir şey
    private fun observeViewModel() {
        viewModelMain.noteList.observe(viewLifecycleOwner) { notes ->
            noteList = ArrayList(notes)
            if (noteList.isEmpty()) {
                binding.rvTasks.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.lottieEmptyState.visibility = View.VISIBLE

            } else {
                binding.rvTasks.visibility = View.VISIBLE
                binding.tvEmptyState.visibility = View.GONE
                binding.lottieEmptyState.visibility = View.GONE
            }

            adapter = NoteAdapter(mutableListOf(), viewModelMain, requireContext(), onItemClick = {


                val bundle = Bundle().apply {
                    putInt("id", it.id)
                    putString("title", it.title)
                    putString("noteText", it.noteText)
                }

                findNavController().navigate(
                    R.id.action_mainFragment_to_detailFragment,
                    bundle
                )


            })

            binding.rvTasks.adapter = adapter
            adapter.updateData(notes)
        }
    }


    //Bu aşamada, binding referansını null yaparak bellek sızıntısını önler
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        viewModelMain.loadNotes()
    }

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModelMain.searchNotes(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

}


