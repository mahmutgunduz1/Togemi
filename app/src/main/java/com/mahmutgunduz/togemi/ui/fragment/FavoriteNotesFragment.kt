package com.mahmutgunduz.togemi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.database.NoteDataBase
import com.mahmutgunduz.togemi.databinding.FragmentFavoriteNotesBinding
import com.mahmutgunduz.togemi.ui.adapter.FavoriteAdapter
import com.mahmutgunduz.togemi.ui.viewmodel.FavoriteNotesViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.FavoriteNotesViewModelFactory

class FavoriteNotesFragment : Fragment() {

    private var _binding: FragmentFavoriteNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteAdapter
    private lateinit var viewModel: FavoriteNotesViewModel
    private lateinit var noteDataBase: NoteDataBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeFavorites()
        setupClickListeners()
    }

    private fun setupViewModel() {
        noteDataBase = NoteDataBase.getInstance(requireContext())
        val noteDao = noteDataBase.noteDao()
        val factory = FavoriteNotesViewModelFactory(noteDao)
        viewModel = ViewModelProvider(this, factory)[FavoriteNotesViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(emptyList()) { noteData ->
            // Not detayına git
            val bundle = Bundle().apply {
                putInt("id", noteData.id)
                putString("title", noteData.title)
                putString("noteText", noteData.noteText)
            }
            findNavController().navigate(R.id.action_favoriteNotesFragment_to_detailFragment, bundle)
        }

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteNotesFragment.adapter
        }
    }

    private fun observeFavorites() {
        viewModel.noteList.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                showEmptyState()
            } else {
                showNotes(favorites)
            }
        }
        // Favori notları yükle
        viewModel.favoriteNotes()
    }

    private fun showEmptyState() {
        binding.apply {
            emptyStateContainer.visibility = View.VISIBLE
            rvTasks.visibility = View.GONE
        }
    }

    private fun showNotes(notes: List<com.mahmutgunduz.togemi.data.entity.NoteData>) {
        binding.apply {
            emptyStateContainer.visibility = View.GONE
            rvTasks.visibility = View.VISIBLE
            adapter.updateList(notes)
        }
    }

    private fun setupClickListeners() {
        // Arama işlevselliği
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFavorites(s?.toString() ?: "")
            }
            
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun filterFavorites(query: String) {
        viewModel.noteList.value?.let { notes ->
            val filteredNotes = notes.filter { note ->
                note.title.contains(query, true) || note.noteText.contains(query, true)
            }
            if (filteredNotes.isEmpty()) {
                showEmptyState()
            } else {
                showNotes(filteredNotes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}