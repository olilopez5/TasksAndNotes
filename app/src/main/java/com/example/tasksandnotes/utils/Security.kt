package com.example.tasksandnotes.utils

import java.security.MessageDigest

object Security {

    fun encryptPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

}