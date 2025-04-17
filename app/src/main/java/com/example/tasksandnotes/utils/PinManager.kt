package com.example.tasksandnotes.utils

import android.content.Context
import android.util.Log

object PinManager {

    private const val PREF_NAME = "pin_prefs"
    private const val PIN_KEY = "user_pin"

    fun savePin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(PIN_KEY, pin).apply()
        Log.d("PinManager", "PIN guardado: $pin")
    }

    fun getStoredPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PIN_KEY, null)

    }

    fun isPinSet(context: Context): Boolean {
        return !getStoredPin(context).isNullOrEmpty()
    }


    fun checkPin(context: Context, enteredPin: String): Boolean {
        return getStoredPin(context)?.trim() == enteredPin.trim()
    }
}









