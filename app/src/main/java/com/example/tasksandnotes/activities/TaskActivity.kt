package com.example.tasksandnotes.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasksandnotes.R
import com.example.tasksandnotes.data.Task
import com.example.tasksandnotes.data.TaskDAO
import com.example.tasksandnotes.databinding.ActivityTaskBinding



class TaskActivity : AppCompatActivity() {

    companion object {
        const val TASK_ID = "TASK_ID"
    }

    lateinit var binding: ActivityTaskBinding

    lateinit var taskDAO: TaskDAO

    lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getLongExtra(TASK_ID, -1L)

        taskDAO = TaskDAO(this)

        if (id != -1L){
            task = taskDAO.findById(id)!!
            binding.titleEditText.setText(task.title)

        }else{
            task = Task(-1L,"")
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()

            task.title = title

            if (task.id != -1L) {
                taskDAO.update(task)
            } else {
                taskDAO.insert(task)
            }

            finish()
        }
    }
}