package com.example.tasksandnotes.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tasksandnotes.adapters.NoteAdapter
import com.example.tasksandnotes.databinding.ActivityPrivateNotesBinding
import com.example.tasksandnotes.data.NoteDAO
import com.example.tasksandnotes.utils.PinDialog
import com.example.tasksandnotes.utils.PinManager

class PrivateNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivateNotesBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var noteDAO: NoteDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si no hay PIN, lo configuramos primero
        if (!PinManager.isPinSet(this)) {
            startActivity(Intent(this, PinSetupActivity::class.java))
            finish()
            return
        }

        // Si hay PIN, pedimos validación antes de mostrar notas
        val storedPin = PinManager.getStoredPin(this) ?: ""
        val pinDialog = PinDialog(this, storedPin) {
            initLayout()
        }
        pinDialog.show()


        loadPrivateNotes()
    }

    private fun initLayout() {
        binding = ActivityPrivateNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDAO = NoteDAO(this)
        loadPrivateNotes()
    }

    private fun loadPrivateNotes() {
        val notes = noteDAO.findPrivateNotes()
        val privateNotes = notes.filter { it.private }
        adapter = NoteAdapter(
            privateNotes,
            onClick = { position ->
                val note = privateNotes[position]
                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra(NoteActivity.NOTE_ID, note.id)
                startActivity(intent)
            },
            onDelete = { position ->
                val note = privateNotes[position]
                noteDAO.delete(note)
                loadPrivateNotes()
            }
        )
        binding.privateNotesRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadPrivateNotes()
    }
}
