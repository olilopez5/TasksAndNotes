package com.example.tasksandnotes.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tasksandnotes.databinding.DialogPinBinding
import com.example.tasksandnotes.databinding.DialogPinInputBinding

class PinDialog(
    private val context: Context,
    private val correctPin: String,
    private val onSuccess: () -> Unit
) {

    fun show() {
        val binding = DialogPinBinding.inflate(LayoutInflater.from(context))

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Introduce tu PIN")
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnSubmit.setOnClickListener {
            val enteredPin = binding.pinEditText.text.toString().trim()
            Log.d("PIN_DEBUG", "enteredPin: '$enteredPin' | correctPin: '$correctPin'")

            val storedPin = correctPin.trim()

            if (enteredPin == storedPin) {
                Toast.makeText(context, "PIN correcto", Toast.LENGTH_SHORT).show()
                Log.d("PIN_DEBUG", "PIN correcto - lanzando onSuccess()")
                onSuccess()
                // Llamar primero
                alertDialog.dismiss() // Luego cerramos el diálogo

            } else {
                Toast.makeText(context, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                Log.d("PIN_DEBUG", "enteredPin: '$enteredPin' | correctPin: '$correctPin'")
            }

        }


        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
