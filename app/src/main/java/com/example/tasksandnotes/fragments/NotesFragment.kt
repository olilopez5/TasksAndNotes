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
import com.example.tasksandnotes.data.Note
import com.example.tasksandnotes.databinding.FragmentNotesBinding
import com.example.tasksandnotes.utils.PinDialog
import com.example.tasksandnotes.utils.PinManager
import com.example.tasksandnotes.data.NoteDAO


class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDAO: NoteDAO
    private var noteList: List<Note> = emptyList()

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
        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.buttonPublic -> {
                    loadPublicNotes()
                }
                R.id.buttonPrivate -> {
                    val storedPin = PinManager.getStoredPin(requireContext())
                    if (storedPin.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), R.string.setup_pin, Toast.LENGTH_SHORT).show()
                        binding.toggleButton.check(R.id.buttonPublic)
                    } else {
                        val dialog = PinDialog(requireContext(), storedPin) {
                            showPrivateNotes()
                        }
                        dialog.show()
                    }
                }
            }
        }


        noteDAO = NoteDAO(requireContext())

        // Configurar el Adapter con las notas privadas
        noteAdapter = NoteAdapter(
            noteList,
            onClick = { position ->
                val note = noteList[position]
                val intent = Intent(requireContext(), NoteActivity::class.java)
                intent.putExtra(NoteActivity.NOTE_ID, note.id)
                startActivity(intent)
            },
            onDelete = { position ->
                val note = noteList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete_note)
                    .setMessage(R.string.delete_dialog)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        noteDAO.delete(note)
                        if (isPrivateNotesVisible) {
                            showPrivateNotes() // Recargar las notas privadas después de eliminar
                        } else {
                            loadPublicNotes()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        )
        binding.notesRecyclerView.adapter = noteAdapter

        // Configurar el RecyclerView para mostrar notas
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(context)
        //binding.privateNotesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Cargar las notas públicas por defecto
        loadPublicNotes()


        // Configurar el click del card para las notas privadas
        binding.privateNotesCard.setOnClickListener {
            val storedPin = PinManager.getStoredPin(requireContext())
            if (storedPin.isNullOrEmpty()) {
                Toast.makeText(requireContext(), R.string.setup_pin, Toast.LENGTH_SHORT).show()
            } else {
                val dialog = PinDialog(requireContext(), storedPin) {
                    Log.d("NOTE_FRAGMENT", "PIN Correcto, mostrando notas privadas.")
                    showPrivateNotes()
                }
                dialog.show()
            }
        }

        return binding.root
    }


    private fun loadPublicNotes() {
        // Obtener todas las notas desde la base de datos
        noteList = noteDAO.findPublicNotes()
        Log.d("NOTE_FRAGMENT", "Notas publicas encontradas: ${noteList.size}")

        // Configurar el Adapter con las notas públicas
        noteAdapter.updateItems(noteList)

        // Establecer el estado de visibilidad de notas privadas
        isPrivateNotesVisible = false
    }



    private fun showPrivateNotes() {

        noteList = noteDAO.findPrivateNotes()
        Log.d("NOTE_FRAGMENT", "Notas privadas encontradas: ${noteList.size}")

        // Configurar el Adapter con las notas privadas
        noteAdapter.updateItems(noteList)

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
    override fun onStart() {
        super.onStart()
        Log.d("NOTE_FRAGMENT", "onStart - refreshPublicNotes- Actualizamos notas publicas")
        refreshPublicNotes()
        Log.d("NOTE_FRAGMENT", "onStart - refreshPrivateNotes- Actualizamos notaas privadas")
        refreshPrivateNotes()
    }


    private fun refreshPublicNotes() {
        val noteDAO = NoteDAO(requireContext())
        val publicNotes = noteDAO.findPublicNotes()
        noteAdapter.updateItems(publicNotes)
    }

    private fun refreshPrivateNotes() {
        val noteDAO = NoteDAO(requireContext())
        val privateNotes = noteDAO.findPrivateNotes()
        noteAdapter.updateItems(privateNotes)
    }


}
