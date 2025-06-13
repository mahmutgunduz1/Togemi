package com.mahmutgunduz.togemi.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.ToDoData
import com.mahmutgunduz.togemi.database.NoteDataBase
import com.mahmutgunduz.togemi.databinding.FragmentTBinding
import com.mahmutgunduz.togemi.databinding.FragmentThingsToDoBinding
import com.mahmutgunduz.togemi.ui.viewmodel.ThingsToDoViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.ThingsToDoViewModelFactory
import com.mahmutgunduz.togemi.ui.viewmodel.ToDoDetailsViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.ToDoDetailsViewModelFactory

class ToDoDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTBinding
    private lateinit var viewModel: ToDoDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = NoteDataBase.getInstance(requireContext()).toDo()
        val factory = ToDoDetailsViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[ToDoDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            val title = binding.taskTitleInput.text.toString()
            val description = binding.taskDescriptionInput.text.toString()
            val dueDate = binding.datePickerButton.text.toString()
            val dueTime = binding.timePickerButton.text.toString()
            val priority = binding.priorityChipGroup.checkedChipId.toString()
            val reminder = binding.reminderSwitch.isChecked.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                viewModel.toDoInsert(ToDoData(title, description))
            }
        }
    }
}

