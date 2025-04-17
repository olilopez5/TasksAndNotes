package com.example.tasksandnotes.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.tasksandnotes.R
import com.example.tasksandnotes.adapters.MainViewPagerAdapter
import com.example.tasksandnotes.adapters.NoteAdapter
import com.example.tasksandnotes.adapters.TaskAdapter
import com.example.tasksandnotes.data.Note
import com.example.tasksandnotes.data.NoteDAO
import com.example.tasksandnotes.data.Task
import com.example.tasksandnotes.data.TaskDAO
import com.example.tasksandnotes.databinding.ActivityMainBinding
import com.example.tasksandnotes.utils.PinManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskDAO: TaskDAO
    private lateinit var noteDAO: NoteDAO

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var pagerAdapter: MainViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.toolbar)

        taskDAO = TaskDAO(this)
        noteDAO = NoteDAO(this)

        // Inicializar el ViewPager2 y el TabLayout
        viewPager = binding.viewPager
        tabLayout = binding.tabs

        // Configurar el adapter para el ViewPager2
        pagerAdapter = MainViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter



        enableEdgeToEdge()


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


         fun setupButtons() {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            binding.viewCalendar.setOnClickListener {
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }

            binding.addNewItem.setOnClickListener {
                when (binding.tabs.selectedTabPosition) {
                    0 -> startActivity(Intent(this, TaskActivity::class.java))
                    1 -> startActivity(Intent(this, NoteActivity::class.java))
                }
            }
        }


        fun setupTabs() {
            // Configurar el ViewPager2 con el adaptador de fragmentos
            val adapter = MainViewPagerAdapter(this)
            binding.viewPager.adapter = adapter

            // Asociar TabLayout con ViewPager2
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.setIcon(R.drawable.ic_tasks_tab)
                        tab.text = "Tasks"
                    }
                    1 -> {
                        tab.setIcon(R.drawable.ic_notes_tab)
                        tab.text = "Notes"
                    }
                }
            }.attach()
        }

        setupButtons()
        setupTabs()
        refreshData()


    }

    fun updateTaskBadge(count: Int) {
        val tasksTab = binding.tabs.getTabAt(0)!!
        if (count > 0) {
            val badge = tasksTab.getOrCreateBadge()
            badge.number = count
        } else {
            tasksTab.removeBadge()
        }
    }

    override fun onResume() {
        super.onResume()
        //refreshData()
    }

    fun refreshData() {
        val toDoTasksNumber = taskDAO.countByNotDone()
        updateTaskBadge(toDoTasksNumber)
    }




//    val toDoTasksNumber = taskDAO.countByNotDone()
//    val tasksTab = binding.tabs.getTabAt(0)!!
//    if (toDoTasksNumber > 0) {
//        val badge = tasksTab.getOrCreateBadge()
//        badge.number = toDoTasksNumber
//    } else {
//        tasksTab.removeBadge()
//    }

}











