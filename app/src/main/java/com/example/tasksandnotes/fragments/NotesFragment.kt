package com.example.tasksandnotes.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasksandnotes.R
import com.example.tasksandnotes.activities.NoteActivity
import com.example.tasksandnotes.activities.PinSetupActivity
import com.example.tasksandnotes.adapters.NoteAdapter
import com.example.tasksandnotes.data.Note
import com.example.tasksandnotes.databinding.FragmentNotesBinding
import com.example.tasksandnotes.utils.PinDialog
import com.example.tasksandnotes.utils.PinManager
import com.example.tasksandnotes.data.NoteDAO
import com.example.tasksandnotes.databinding.DialogPinBinding


class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDAO: NoteDAO

    private var isPrivateNotesVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        if (!PinManager.isPinSet(requireContext())) {
            startActivity(Intent(requireContext(), PinSetupActivity::class.java))
            //TODO
        }


        // Configurar el RecyclerView para mostrar notas
        binding.publicNotesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Cargar las notas públicas por defecto
        loadPublicNotes()

        // Configurar el click del card para las notas privadas
        binding.privateNotesCard.setOnClickListener {
            val storedPin = PinManager.getStoredPin(requireContext())
            if (storedPin.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Primero configura un PIN", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = PinDialog(requireContext(), storedPin) {
                    // Si el PIN es correcto, mostrar las notas privadas
                    showPrivateNotes()
                }
                dialog.show()
            }
        }

        return binding.root
    }

    private fun loadPublicNotes() {
        // Obtener todas las notas desde la base de datos
        noteDAO = NoteDAO(requireContext())

        val notes = noteDAO.findAll()
        val publicNotes = notes.filter { !it.private }

        // Configurar el Adapter con las notas públicas
        noteAdapter = NoteAdapter(
            publicNotes,
            onClick = { position ->
                val note = publicNotes[position]
                val intent = Intent(requireContext(), NoteActivity::class.java)
                intent.putExtra(NoteActivity.NOTE_ID, note.id)
                startActivity(intent)
            },
            onDelete = { position ->
                val note = publicNotes[position]
                noteDAO.delete(note)
                loadPublicNotes() // Recargar las notas públicas después de eliminar
            }
        )

        binding.publicNotesRecyclerView.adapter = noteAdapter
    }

    private fun showPrivateNotes() {
        // Obtener todas las notas desde la base de datos
        val notes = noteDAO.findAll()
        val privateNotes = notes.filter { it.private }

        fun setupAdapters() {


            noteAdapter = NoteAdapter(
                emptyList(),
                onClick = { position ->
                    val note = noteAdapter.items[position]
                    if (note.private) {
                        showPinDialog { enteredPin ->
                            if (PinManager.checkPin(requireContext(), enteredPin)) {
                                openNote(note)
                            } else {
                                Toast.makeText(requireContext(), "PIN incorrecto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        openNote(note)
                    }
                },
                onDelete = { position ->
                    val note = noteAdapter.items[position]
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            noteDAO.delete(note)
                            refreshData()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            )
        }

        binding.publicNotesRecyclerView.adapter = noteAdapter
    }

    override fun onResume() {
        super.onResume()

        // Si estamos mostrando las notas privadas, las recargamos
        if (isPrivateNotesVisible) {
            showPrivateNotes()
        } else {
            loadPublicNotes()
        }
    }

    private fun showPinDialogThenLoadPrivateNotes() {
        val storedPin = PinManager.getStoredPin(requireContext())
        if (storedPin.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No hay PIN guardado", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = PinDialog(
            context = requireContext(),
            correctPin = storedPin,
            onSuccess = {
                loadPrivateNotes()
            }
        )
        dialog.show()
    }

    fun showPinDialog(onPinEntered: (String) -> Unit) {
        val dialogBinding = DialogPinBinding.inflate(layoutInflater)
        val pinEditText: EditText = dialogBinding.pinEditText

        AlertDialog.Builder(requireContext())
            .setTitle("Ingrese su PIN")
            .setView(dialogBinding.root)
            .setPositiveButton("Aceptar") { _, _ ->
                val enteredPin = pinEditText.text.toString()
                onPinEntered(enteredPin)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

//    private fun loadPublicNotes() {
//        val publicNotes = noteDAO.findPublicNotes()
//        noteAdapter.updateItems(publicNotes)
//    }

    private fun loadPrivateNotes() {
        val privateNotes = noteDAO.findPrivateNotes()
        noteAdapter.updateItems(privateNotes)
    }

    private fun openNote(note: Note) {
        val intent = Intent(requireContext(), NoteActivity::class.java)
        intent.putExtra(NoteActivity.NOTE_ID, note.id)
        startActivity(intent)
    }

    private fun refreshData() {
        val noteDAO = NoteDAO(requireContext())
        val notes = noteDAO.findAll()
        noteAdapter.updateItems(notes)
}
}


