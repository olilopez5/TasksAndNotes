package com.example.tasksandnotes.utils

import android.content.Context

object PinManager {

    private const val PREF_NAME = "app_preferences"
    private const val PIN_KEY = "user_pin"

    // Guardar el PIN en SharedPreferences
    fun savePin(context: Context, pinHash: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(PIN_KEY, pinHash)
        editor.apply()
    }

    // Verificar si el PIN ya está configurado
    fun isPinSet(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.contains(PIN_KEY)
    }

    // Verificar el PIN ingresado por el usuario
    fun checkPin(context: Context, enteredPin: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val storedPinHash = sharedPreferences.getString(PIN_KEY, null)

        if (storedPinHash != null) {
            val enteredPinHash = Security.encryptPassword(enteredPin)
            return enteredPinHash == storedPinHash
        }
        return false
    }
}
