package com.example.tasksandnotes.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasksandnotes.R
import com.example.tasksandnotes.data.Note
import com.example.tasksandnotes.data.NoteDAO
import com.example.tasksandnotes.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {

    companion object {
        const val NOTE_ID = "NOTE_ID"
    }

    lateinit var binding: ActivityNoteBinding

    lateinit var noteDAO: NoteDAO

    lateinit var note: Note
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val id = intent.getLongExtra(NOTE_ID, -1L)

        noteDAO = NoteDAO(this)

        if (id != -1L){
            note = noteDAO.findById(id)!!
            binding.noteTitleEditText.setText(note.title)
            binding.noteDescriptionEditText.setText(note.description)
            binding.privateNoteSwitch.isChecked = note.private
        }else{
            note = Note(-1L,"", "", 0)
        }

        binding.saveNoteButton.setOnClickListener {
            val title = binding.noteTitleEditText.text.toString()
            val description = binding.noteDescriptionEditText.text.toString()
            val private = binding.privateNoteSwitch.isChecked

            note.title = title
            note.description = description
            note.private = private

            if (note.id != -1L) {
                noteDAO.update(note)
            } else {
                noteDAO.insert(note)
            }

            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}