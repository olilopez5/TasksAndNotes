package com.example.tasksandnotes.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tasksandnotes.databinding.DialogPinInputBinding
import com.example.tasksandnotes.utils.PinManager
import com.example.tasksandnotes.utils.Security

class PinSetupActivity : AppCompatActivity() {
    private lateinit var binding: DialogPinInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogPinInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val pin = binding.pinEditText.text.toString()

            if (pin.length < 6) {
                Toast.makeText(this, "El PIN debe tener al menos 6 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encryptedPin = Security.encryptPassword(pin)
            PinManager.savePin(this, encryptedPin)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
