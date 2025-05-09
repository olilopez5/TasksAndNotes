package com.example.tasksandnotes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasksandnotes.R
import com.example.tasksandnotes.activities.MainActivity
import com.example.tasksandnotes.adapters.TaskAdapter
import com.example.tasksandnotes.data.Task
import com.example.tasksandnotes.data.TaskDAO
import com.example.tasksandnotes.databinding.FragmentTasksBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDAO: TaskDAO
    private lateinit var taskList: List<Task>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        binding = FragmentTasksBinding.inflate(inflater, container, false)
        Log.d("TASK_FRAGMENT", "onCreateView")
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        taskDAO = TaskDAO(requireContext())

        taskAdapter = TaskAdapter(
            emptyList(),
            onClick = { position ->
                val task = taskList[position]
                showEditTaskDialog(task)
            },
            onDelete = { position ->
                val task = taskList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete_note)
                    .setMessage(R.string.delete_dialog)
                    .setPositiveButton(R.string.accept_button) { _, _ ->
                        taskDAO.delete(task)
                        refreshData()
                        Log.d("TASK_FRAGMENT", "Tarea borrada: quedan ${taskList.size} notas")
                    }
                    .setNegativeButton(R.string.cancel_button, null)
                    .show()
            },
            onCheck = { position ->
                val task = taskList[position]
                task.done = !task.done
                if (task.done) {
                    task.priority = 0
                }
                taskDAO.update(task)
                refreshData()
                Log.d("TASK_FRAGMENT", "Tarea hecha y sin prioridad")
            },
            context = requireContext()
        )

        binding.recyclerViewTasks.adapter = taskAdapter

        loadTasks()

        return binding.root
    }

    fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)
        val editText = dialogView.findViewById<TextInputLayout>(R.id.textField)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerPriority)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_labels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_task_dialog)
            .setView(dialogView)
            .setPositiveButton(R.string.save_button) { _, _ ->
                val title = editText.editText?.text.toString().trim()
                val priority = spinner.selectedItemPosition
                if (title.isNotEmpty()) {
                    val task = Task(id = -1L, title = title, priority = priority)
                    taskDAO.insert(task)
                    refreshData()
                }
            }
            .setNegativeButton(R.string.cancel_button, null)
            .show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)
        val editText = dialogView.findViewById<TextInputLayout>(R.id.textField)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerPriority)

        editText.editText?.setText(task.title)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_labels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.setSelection(task.priority)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_task)
            .setView(dialogView)
            .setPositiveButton(R.string.save_button) { _, _ ->
                val newTitle = editText.editText?.text.toString().trim()
                val newPriority = spinner.selectedItemPosition
                if (newTitle.isNotEmpty()) {
                    task.title = newTitle
                    task.priority = newPriority
                    Log.d("TASK_FRAGMENT", "Nueva prioridad: ${task.priority}")
                    taskDAO.update(task)
                    refreshData()
                }
            }
            .setNegativeButton(R.string.cancel_button, null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        Log.d("TASK_FRAGMENT", "RefreshData - Actualizamos los datos")
        refreshData()
    }

    private fun refreshData() {
        val taskDAO = TaskDAO(requireContext())
        val tasks = taskDAO.findAll()
        taskList = tasks
        taskAdapter.updateItems(tasks)

        val countNotDone = taskDAO.countByNotDone()
        (activity as? MainActivity)?.updateTaskBadge(countNotDone)
    }

    private fun loadTasks() {
        taskList = taskDAO.findAll()
        Log.d("TASK_FRAGMENT", "Tareas cargadas: ${taskList.size}")
        taskAdapter.updateItems(taskList)
    }
}