package com.example.tasksandnotes.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasksandnotes.R
import com.example.tasksandnotes.activities.MainActivity
import com.example.tasksandnotes.activities.TaskActivity
import com.example.tasksandnotes.adapters.TaskAdapter
import com.example.tasksandnotes.data.TaskDAO
import com.example.tasksandnotes.databinding.FragmentTasksBinding
import com.example.tasksandnotes.data.Task

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
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        // Inicializa el DAO y el adaptador
        taskDAO = TaskDAO(requireContext())


        taskAdapter = TaskAdapter(
            emptyList(),
            onClick = { position ->
                val task = taskList[position]
                val intent = Intent(requireContext(), TaskActivity::class.java)
                intent.putExtra(TaskActivity.TASK_ID, task.id)
                startActivity(intent)
            },
            onDelete = { position ->
                val task = taskList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        taskDAO.delete(task)
                        refreshData()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            },
            onCheck = { position ->
                val task = taskList[position]
                task.done = !task.done
                taskDAO.update(task)
                refreshData()
            }
        )
        binding.recyclerViewTasks.adapter = taskAdapter


        loadTasks()

        return binding.root
    }

    fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)
        val editText = dialogView.findViewById<android.widget.EditText>(R.id.editTextTaskTitle)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    val task = Task(id = -1L, title = title)
                    taskDAO.insert(task)
                    refreshData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        Log.d("refreshData", "Actualizamos los datos")
        refreshData()
    }


    private fun refreshData() {
        val taskDAO = TaskDAO(requireContext())
        val tasks = taskDAO.findAll()
        taskAdapter.updateItems(tasks)

        // Puedes notificar a la Activity para actualizar el badge si lo deseas:
        val countNotDone = taskDAO.countByNotDone()
        (activity as? MainActivity)?.updateTaskBadge(countNotDone)
    }


    private fun loadTasks() {
        taskList = taskDAO.findAll()
        Log.d("TasksFragment", "Tareas cargadas: ${taskList.size}")  // Añade esto
        taskAdapter.updateItems(taskList)
    }
}
