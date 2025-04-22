package com.example.tasksandnotes.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tasksandnotes.databinding.DialogPinInputBinding
import com.example.tasksandnotes.utils.PinManager


class PinSetupActivity : AppCompatActivity() {
    private lateinit var binding: DialogPinInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogPinInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val pin = binding.pinEditText.text.toString()

            if (pin.length != 6) {
                Toast.makeText(this, "El PIN debe tener exactamente 6 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            if (!pin.all { it.isDigit() }) {
//                Toast.makeText(this, "El PIN solo puede contener números", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val encryptedPin = Security.encryptPassword(pin)
//            PinManager.savePin(this, encryptedPin)

            PinManager.savePin(this, pin)

            Toast.makeText(this, "PIN guardado correctamente", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("goToNotesTab", true)
            startActivity(intent)
            finish()

        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
