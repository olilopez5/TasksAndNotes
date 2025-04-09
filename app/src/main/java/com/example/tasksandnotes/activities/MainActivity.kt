package com.example.tasksandnotes.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasksandnotes.adapters.TaskAdapter
import com.example.tasksandnotes.data.Task
import com.example.tasksandnotes.data.TaskDAO
import com.example.tasksandnotes.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var taskDAO: TaskDAO

    lateinit var taskList : List<Task>

    lateinit var taskAdapter: TaskAdapter
    //lateinit var noteAdapter: NoteAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Crear el MaterialDatePicker
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Fecha actual
            .build()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDAO = TaskDAO(this)

        taskAdapter = TaskAdapter(
            emptyList(),
            { position ->
                val task = taskList[position]

                val intent = Intent(this, TaskActivity::class.java)
                intent.putExtra(TaskActivity.TASK_ID, task.id)
                startActivity(intent)
            },
            { position ->
                val task = taskList[position]

                AlertDialog.Builder(this)
                    .setTitle("Delete task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        taskDAO.delete(task)
                        refreshData()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(false)
                    .show()

            },
            { position ->
                val task = taskList[position]

                task.done = !task.done
                taskDAO.update(task)
                refreshData()

            },
        )


        binding.recyclerView.adapter = taskAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addNewItem.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }


        binding.tabs.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (binding.tabs.selectedTabPosition) {
                    0 -> {
                        // Cargo las tareas
                        binding.recyclerView.adapter = taskAdapter
                    }
                    1 -> {
                        // Cargo las notas
                        //binding.recyclerView.adapter = noteAdapter
                    }
                }
                //  supportActionBar?.title = tab.text
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        binding.viewCalendar.setOnClickListener {
            materialDatePicker.show(supportFragmentManager, "DATE_PICKER")
        }



        binding.addNewItem.setOnClickListener { view ->
            when (binding.tabs.selectedTabPosition) {
                0 -> {
                    // Creo una tarea
                    val intent = Intent(this, TaskActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    // Creo una nota
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        taskList = taskDAO.findAll()
        taskAdapter.updateItems(taskList)

        val toDoTasksNumber = taskDAO.countByNotDone()
        val tasksTab = binding.tabs.getTabAt(0)!!
        if (toDoTasksNumber > 0) {
            // Get badge from tab (or create one if none exists)
            val badge = tasksTab.getOrCreateBadge()
            // Customize badge
            badge.number = toDoTasksNumber
        } else {
            // Remove badge from tab
            tasksTab.removeBadge()
        }
    }
}