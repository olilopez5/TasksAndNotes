package com.example.tasksandnotes.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasksandnotes.R
import com.example.tasksandnotes.activities.NoteActivity
import com.example.tasksandnotes.activities.PinSetupActivity
import com.example.tasksandnotes.adapters.NoteAdapter
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
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        if (!PinManager.isPinSet(requireContext())) {
            startActivity(Intent(requireContext(), PinSetupActivity::class.java))
            return binding.root
        }

        // Configurar el RecyclerView para mostrar notas
        binding.publicNotesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.privateNotesRecyclerView.layoutManager = LinearLayoutManager(context)

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
                    Log.d("PinValidation", "PIN Correcto, mostrando notas privadas.")
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

        // Establecer el estado de visibilidad de notas privadas
        isPrivateNotesVisible = false
    }





    private fun showPrivateNotes() {

        val privateNotes = noteDAO.findPrivateNotes()
        Log.d("PrivateNotes", "Notas privadas encontradas: ${privateNotes.size}")

        // Configurar el Adapter con las notas privadas
        noteAdapter = NoteAdapter(
            privateNotes,
            onClick = { position ->
                val note = privateNotes[position]
                val intent = Intent(requireContext(), NoteActivity::class.java)
                intent.putExtra(NoteActivity.NOTE_ID, note.id)
                startActivity(intent)
            },
            onDelete = { position ->
                val note = privateNotes[position]
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        noteDAO.delete(note)
                        showPrivateNotes() // Recargar las notas privadas después de eliminar
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        )
        binding.privateNotesRecyclerView.adapter = noteAdapter

        // Asegurarnos de que el RecyclerView de notas privadas esté visible
        binding.privateNotesRecyclerView.visibility = View.VISIBLE
        binding.publicNotesRecyclerView.visibility = View.GONE // Si quieres ocultar las notas públicas

        // Notificar al adaptador que los datos han cambiado
        noteAdapter.notifyDataSetChanged()

        // Establecer el estado de visibilidad de notas privadas
        isPrivateNotesVisible = true
    }

    override fun onResume() {
        super.onResume()

        // Recargar notas públicas o privadas al volver al Fragment
        if (isPrivateNotesVisible) {
            showPrivateNotes()
        } else {
            loadPublicNotes()
        }
    }
}



