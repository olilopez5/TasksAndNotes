package com.example.tasksandnotes.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.tasksandnotes.utils.DatabaseManager

class NoteDAO(context: Context) {

    val databaseManager = DatabaseManager(context)

    fun insert(note: Note) {
        val db = databaseManager.writableDatabase
        val values = ContentValues().apply {
            put(Note.COLUMN_NAME_TITLE, note.title)
            put(Note.COLUMN_NAME_DESCRIPTION, note.description)
            put(Note.COLUMN_NAME_DATE, note.date)
            put(Note.COLUMN_NAME_PRIVATE, note.private)
        }
        try {
            val newRowId = db.insert(Note.TABLE_NAME, null, values)
            Log.i("DATABASE", "Insert : $newRowId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun update(note: Note) {
        val db = databaseManager.writableDatabase
        val values = ContentValues().apply {
            put(Note.COLUMN_NAME_TITLE, note.title)
            put(Note.COLUMN_NAME_DESCRIPTION, note.description)
            put(Note.COLUMN_NAME_DATE, note.date)
            put(Note.COLUMN_NAME_PRIVATE, note.private)
        }
        try {
            db.update(Note.TABLE_NAME, values, "${Note.COLUMN_NAME_ID} = ${note.id}", null)
            Log.i("DATABASE", "Update note: ${note.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun delete(note: Note) {
        val db = databaseManager.writableDatabase
        try {
            db.delete(Note.TABLE_NAME, "${Note.COLUMN_NAME_ID} = ${note.id}", null)
            Log.i("DATABASE", "Delete note: ${note.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun findById(id: Long): Note? {
        val db = databaseManager.readableDatabase
        val projection = arrayOf(
            Note.COLUMN_NAME_ID,
            Note.COLUMN_NAME_TITLE,
            Note.COLUMN_NAME_DESCRIPTION,
            Note.COLUMN_NAME_DATE,
            Note.COLUMN_NAME_PRIVATE
        )
        val selection = "${Note.COLUMN_NAME_ID} = $id"
        var note: Note? = null
        try {
            val cursor = db.query(
                Note.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
            )
            if (cursor.moveToNext()) {
                note = cursorToNote(cursor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return note
    }

    fun findAll(): List<Note> {
        val db = databaseManager.readableDatabase
        val noteList: MutableList<Note> = mutableListOf()
        try {
            val cursor = db.query(
                Note.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Note.COLUMN_NAME_DESCRIPTION
            )
            while (cursor.moveToNext()) {
                val note = cursorToNote(cursor)
                noteList.add(note)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return noteList
    }

    fun findPublicNotes(): List<Note> {
        val db = databaseManager.readableDatabase
        val publicNoteList: MutableList<Note> = mutableListOf()
        val selection = "${Note.COLUMN_NAME_PRIVATE} = 0"
        try {
            val cursor = db.query(
                Note.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                val note = cursorToNote(cursor)
                publicNoteList.add(note)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return publicNoteList
    }

    fun findPrivateNotes(): List<Note> {
        val db = databaseManager.readableDatabase
        val noteList: MutableList<Note> = mutableListOf()
        val selection = "${Note.COLUMN_NAME_PRIVATE} = 1"
        try {
            val cursor = db.query(
                Note.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                val note = cursorToNote(cursor)
                noteList.add(note)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return noteList
    }

    fun sortedByDate(ascending: Boolean = true): List<Note> {
        val db = databaseManager.readableDatabase
        val noteList: MutableList<Note> = mutableListOf()
        val order = if (ascending) "ASC" else "DESC"
        try {
            val cursor = db.query(
                Note.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "${Note.COLUMN_NAME_DATE} $order"
            )
            while (cursor.moveToNext()) {
                val note = cursorToNote(cursor)
                noteList.add(note)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return noteList
    }

    private fun cursorToNote(cursor: Cursor): Note {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_TITLE))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DESCRIPTION))
        val date = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DATE))
        val private = cursor.getInt(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_PRIVATE)) == 1
        return Note(id, title, description, date, private)
    }
}