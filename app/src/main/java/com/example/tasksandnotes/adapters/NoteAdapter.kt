package com.example.tasksandnotes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tasksandnotes.data.Note
import com.example.tasksandnotes.databinding.ItemNoteBinding

class NoteAdapter(var items: List<Note>,
                  val onClick: (Int) -> Unit,
                  val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = items[position]
        holder.render(note)
        holder.itemView.setOnClickListener {
            onClick(position)
        }
        holder.binding.editButton.setOnClickListener {
            onClick(position)
        }
        holder.binding.deleteButton.setOnClickListener {
            onDelete(position)
        }
    }

    fun updateItems(items: List<Note>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class NoteViewHolder(val binding: ItemNoteBinding) : ViewHolder(binding.root) {

    fun render(note: Note) {

        if (note.private) {
            binding.titleTextView.text = note.title
        } else {
            binding.titleTextView.text = note.title
        }
    }
}