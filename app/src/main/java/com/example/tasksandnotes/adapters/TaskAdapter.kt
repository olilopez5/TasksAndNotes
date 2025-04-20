package com.example.tasksandnotes.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tasksandnotes.databinding.ItemTaskBinding
import com.example.tasksandnotes.utils.addStrikethrough
import com.example.tasksandnotes.data.Task

class TaskAdapter(var items: List<Task>,
                  val onClick: (Int) -> Unit,
                  val onDelete: (Int) -> Unit,
                  val onCheck: (Int) -> Unit
    ) : Adapter<TaskViewHolder>() {

//    private val priorityColors = mapOf(
//        "Low" to Color.GREEN,       // Verde
//        "Medium" to Color.parseColor("#FF9800"), // Naranja
//        "High" to Color.RED        // Rojo
//    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.render(task)

//        val priorityColor = priorityColors[task.priority] ?: Color.GREEN
//        holder.binding.priorityCircle.setColorFilter(priorityColor)

        holder.itemView.setOnClickListener {
            onClick(position)
        }
        holder.binding.deleteButton.setOnClickListener {
            onDelete(position)
        }
        holder.binding.doneCheckBox.setOnCheckedChangeListener { _, _ ->
            if (holder.binding.doneCheckBox.isPressed) {
                onCheck(position)
            }
        }
    }

    fun updateItems(items: List<Task>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class TaskViewHolder(val binding: ItemTaskBinding) : ViewHolder(binding.root) {

    fun render(task: Task) {
        binding.doneCheckBox.isChecked = task.done

        if (task.done) {
            binding.titleTextView.text = task.title.addStrikethrough()
        } else {
            binding.titleTextView.text = task.title
        }
    }
}