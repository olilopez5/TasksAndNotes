package com.example.tasksandnotes.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tasksandnotes.R
import com.example.tasksandnotes.databinding.ItemTaskBinding
import com.example.tasksandnotes.utils.addStrikethrough
import com.example.tasksandnotes.data.Task

class TaskAdapter(var items: List<Task>,
                  val onClick: (Int) -> Unit,
                  val onDelete: (Int) -> Unit,
                  val onCheck: (Int) -> Unit,
                  val context: Context
) : Adapter<TaskViewHolder>() {

    // Acceder a los arrays definidos en el archivo XML de recursos
    private val priorityLabels = context.resources.getStringArray(R.array.priority_labels)
    private val priorityColors = context.resources.getIntArray(R.array.priority_colors)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.render(task)

// Asignar el color de la prioridad usando la posición
        val priorityColor = if (task.priority in 1..3) {
            priorityColors[task.priority] // Obtener el color según el valor de la prioridad
        } else {
            Color.TRANSPARENT // Sin prioridad
        }

        // Establecer el color de fondo del círculo de prioridad
        holder.binding.priorityCircle.setBackgroundColor(priorityColor)


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
        // Círculo de color según prioridad
//        val priorityColor = when (task.priority) {
//            1 -> Color.parseColor("#4CAF50") // Verde (baja)
//            2 ->Color.parseColor("#FF9800") // Naranja (media)
//            3 -> Color.parseColor("#F44336") // Rojo (alta)
//            else -> Color.TRANSPARENT // Sin prioridad
//        }
//
//        binding.priorityCircle.background.setTint(priorityColor)
   }
}
