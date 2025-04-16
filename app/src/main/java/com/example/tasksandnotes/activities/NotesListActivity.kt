package com.example.tasksandnotes.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasksandnotes.adapters.NoteAdapter
import com.example.tasksandnotes.data.NoteDAO
import com.example.tasksandnotes.databinding.ActivityNotesListBinding
import com.example.tasksandnotes.utils.PinDialog
import com.example.tasksandnotes.utils.PinManager


class NotesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesListBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDAO: NoteDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configurar el RecyclerView
        binding.publicNotesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar las notas públicas
        loadPublicNotes()

        // Configurar el click para notas privadas
        binding.privateNotesCard.setOnClickListener {
            val storedPin = PinManager.getStoredPin(this)
            if (storedPin.isNullOrEmpty()) {
                Toast.makeText(this, "Primero configura un PIN", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = PinDialog(this, storedPin) {
                    // Si el PIN es correcto, abrir las notas privadas
                    startActivity(Intent(this, PrivateNotesActivity::class.java))
                }
                dialog.show()
            }
        }
    }

    private fun loadPublicNotes() {
        // Obtener todas las notas desde la base de datos
        val notes = noteDAO.findAll()
        val publicNotes = notes.filter { !it.private }

        // Configurar el adapter con las notas públicas
        noteAdapter = NoteAdapter(
            publicNotes,
            onClick = { index ->
                val note = publicNotes[index]
                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra(NoteActivity.NOTE_ID, note.id)
                startActivity(intent)
            },
            onDelete = { index ->
                val note = publicNotes[index]
                noteDAO.delete(note)
                loadPublicNotes() // Recargar las notas públicas después de eliminar
            }
        )

        binding.publicNotesRecyclerView.adapter = noteAdapter
    }

    override fun onResume() {
        super.onResume()
        loadPublicNotes() // Recargar notas cuando regresas a la actividad
    }
}
