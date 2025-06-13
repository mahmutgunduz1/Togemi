package com.mahmutgunduz.togemi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.database.NoteDataBase
import com.mahmutgunduz.togemi.databinding.FragmentMainBinding
import com.mahmutgunduz.togemi.databinding.FragmentTBinding
import com.mahmutgunduz.togemi.databinding.FragmentThingsToDoBinding
import com.mahmutgunduz.togemi.ui.viewmodel.ThingsToDoViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.ThingsToDoViewModelFactory


class ThingsToDoFragment : Fragment() {

    private lateinit var viewModel: ThingsToDoViewModel
    private lateinit var binding: FragmentThingsToDoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = NoteDataBase.getInstance(requireContext()).toDo()
        val factory = ThingsToDoViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[ThingsToDoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThingsToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_toDoFragment_to_toDoDetailsFragment)
        }
    }
}