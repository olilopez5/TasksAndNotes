package com.example.tasksandnotes.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tasksandnotes.activities.MainActivity
import com.example.tasksandnotes.fragments.TasksFragment
import com.example.tasksandnotes.fragments.NotesFragment

class MainViewPagerAdapter(fragment: MainActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TasksFragment() // Pestaña de tareas
            1 -> NotesFragment() // Pestaña de notas
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
