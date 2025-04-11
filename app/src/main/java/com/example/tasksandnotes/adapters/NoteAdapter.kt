package com.example.tasksandnotes.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tasksandnotes.activities.NoteActivity
import com.example.tasksandnotes.data.Note
import com.example.tasksandnotes.databinding.ItemNoteBinding
import com.example.tasksandnotes.utils.PinDialog

class NoteAdapter(
    private var items: List<Note>,
    //private var privateItems: List<Note>,
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
        holder.binding.privateFolderButton.setOnClickListener{
            onClick(position)
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

            // Agregar un listener para solicitar la contraseña
            binding.root.setOnClickListener {
                showPinDialog(note)
            }
        } else {
            // Nota pública
            binding.titleTextView.text = note.title
        }
    }

    private fun showPinDialog(note: Note) {
        // Aquí estamos pasando el contexto de la actividad y la contraseña de la nota
        val dialog = PinDialog(itemView.context, note.password ?: "")
        dialog.show()
    }

    private fun openNote(note: Note) {
        // Aquí puedes abrir la nota o hacer cualquier otra acción si no es privada
        val intent = Intent(itemView.context, NoteActivity::class.java)
        intent.putExtra(NoteActivity.NOTE_ID, note.id)
        itemView.context.startActivity(intent)
    }
}