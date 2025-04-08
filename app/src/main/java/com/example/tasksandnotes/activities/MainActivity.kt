package com.example.tasksandnotes.activities

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasksandnotes.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tabs.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (binding.tabs.selectedTabPosition) {
                    0 -> {
                        // Cargo las tareas
                    }
                    1 -> {
                        // Cargo las notas
                    }
                }
                //  supportActionBar?.title = tab.text
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        binding.viewCalendar.setOnClickListener {
            // Voy al Activity de calendario
        }

        binding.addNewItem.setOnClickListener { view ->
            when (binding.tabs.selectedTabPosition) {
                0 -> {
                    // Creo una tarea
                }
                1 -> {
                    // Creo una nota
                }
            }
        }

        // Get badge from tab (or create one if none exists)
        val badge = binding.tabs.getTabAt(0)!!.getOrCreateBadge()
        // Customize badge
        badge.number = 2
    }
}