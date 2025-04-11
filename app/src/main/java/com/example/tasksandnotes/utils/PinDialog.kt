package com.example.tasksandnotes.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tasksandnotes.databinding.DialogPinInputBinding

class PinDialog(private val context: Context, private val correctPin: String) {

    fun show() {
        fun show() {
            // Inflamos el layout usando ViewBinding
            val binding = DialogPinInputBinding.inflate(LayoutInflater.from(context))

            // Accedemos al EditText utilizando el binding
            val pinEditText = binding.pinEditText
            val btnSubmit = binding.btnSubmit
            val btnCancel = binding.btnCancel
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Enter PIN")
                .setView(binding.root)
                .setCancelable(false) // Hacemos que no se pueda cerrar tocando fuera del cuadro
                .setPositiveButton("Submit") { dialog, _ ->
                    val enteredPin = pinEditText.text.toString()
                    if (enteredPin == correctPin) {
                        // El PIN es correcto, se puede realizar la acción deseada
                        Toast.makeText(context, "PIN Correcto", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()  // Cerramos el diálogo
                    } else {
                        // El PIN es incorrecto
                        Toast.makeText(context, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()  // Cerrar el cuadro de diálogo si se cancela
                }

            alertDialog.show()
        }
    }
}
