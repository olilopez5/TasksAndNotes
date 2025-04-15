package com.example.tasksandnotes.data

import java.util.Calendar

data class Note(
    var id: Long,
    var title: String,
    var description: String,
    var date: Long,
    var private : Boolean = true

) {
    companion object{
        const val TABLE_NAME = "Notes"

        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_PRIVATE = "private"


    }


    fun getCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        return calendar
    }

    fun setCalendar(calendar: Calendar) {
        date = calendar.timeInMillis
    }
}