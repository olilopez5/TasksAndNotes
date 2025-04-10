package com.example.tasksandnotes.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.tasksandnotes.utils.DatabaseManager
import com.example.tasksandnotes.utils.Security



class NoteDAO(context: Context) {

    val databaseManager = DatabaseManager(context)

    fun insert(note: Note){
        //data in write mode
        val db = databaseManager.writableDatabase
        //new map of values column name is key
        val values = ContentValues().apply {
            put(Note.COLUMN_NAME_TITLE,note.title)
            put(Note.COLUMN_NAME_DESCRIPTION,note.description)
            put(Note.COLUMN_NAME_DATE, note.date)
            put(Note.COLUMN_NAME_PRIVATE, note.private)

//            if (note.private && !note.password.isNullOrEmpty()) {
//                put(Note.COLUMN_NAME_PASSWORD, Security.encryptPassword(note.password!!))
//            }
        }
        // new row, return PK od new row (insert ? error)
        try {
            val newRowId = db.insert(Note.TABLE_NAME, null,values)

            Log.i("DATABASE", "Insert : $newRowId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }


    }
    fun update(note: Note){

        val db = databaseManager.writableDatabase

        val values = ContentValues().apply {
            put(Note.COLUMN_NAME_TITLE,note.title)
            put(Note.COLUMN_NAME_DESCRIPTION,note.description)
            put(Note.COLUMN_NAME_DATE, note.date)
            put(Note.COLUMN_NAME_PRIVATE, note.private)

//            if (note.private && !note.password.isNullOrEmpty()) {
//                put(Note.COLUMN_NAME_PASSWORD, Security.encryptPassword(note.password!!))
//            }
        }
        try {
            //  whereClause = ? , whereArgs arrayOf("${note.id}")
            val newRowId = db.update(Note.TABLE_NAME, values, "${Note.COLUMN_NAME_ID} = ${note.id} ", null)

            Log.i("DATABASE", "Update note: ${note.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

    }
    fun delete(note: Note){

        val db = databaseManager.writableDatabase

        try {
            val deleteRows = db.delete(Note.TABLE_NAME,"${Note.COLUMN_NAME_ID} = ${note.id}", null)

            Log.i("DATABASE", "Delete note: ${note.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
    fun findById(id: Long): Note?{
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
                Note.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            // moveToNext(Boolean) data true, no data false
            //INDEX all columns

            if (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DESCRIPTION))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DATE))
                val private = cursor.getInt(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_PRIVATE)) == 1

                note = Note(id, title, description, date, private)


            }// Si la nota es privada y tiene una contraseña, pedirla al usuario
            /*if (note != null) {
                if (note.isPasswordProtected()) {
                    // Aquí se pedirá la contraseña
                    val passwordCorrect = promptForPassword(context, note?.password!!)
                    if (!passwordCorrect) {
                        note = null  // Si la contraseña no es correcta, devolvemos null
                    }

                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return note
    }

    fun findAll(): List<Note> {
        val db = databaseManager.readableDatabase

        val projection = arrayOf(
            Note.COLUMN_NAME_ID,
            Note.COLUMN_NAME_TITLE,
            Note.COLUMN_NAME_DESCRIPTION,
            Note.COLUMN_NAME_DATE,
            Note.COLUMN_NAME_PRIVATE
        )

        var noteList: MutableList<Note> = mutableListOf()

        try {
            val cursor = db.query(
                Note.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                Note.COLUMN_NAME_DESCRIPTION             // The sort order
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DESCRIPTION))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DATE))
                val private = cursor.getInt(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_PRIVATE)) == 1

                val note = Note(id, title, description, date, private)
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

        val projection = arrayOf(
            Note.COLUMN_NAME_ID,
            Note.COLUMN_NAME_TITLE,
            Note.COLUMN_NAME_DESCRIPTION,
            Note.COLUMN_NAME_DATE,
            Note.COLUMN_NAME_PRIVATE
        )

        val selection = "${Note.COLUMN_NAME_PRIVATE} = 0"  // 0 para false, 1 para true

        val noteList: MutableList<Note> = mutableListOf()

        try {
            val cursor = db.query(
                Note.TABLE_NAME,   // La tabla de notas
                projection,        // Las columnas a devolver
                selection,         // Condición para notas públicas
                null,              // No hay argumentos adicionales
                null,              // No agrupar filas
                null,              // No filtrar por grupos de filas
                null               // Sin ordenar (lo ordenaremos después si lo deseamos)
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DESCRIPTION))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DATE))
                val private = cursor.getInt(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_PRIVATE)) == 1

                val note = Note(id, title, description, date, private)
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

        val projection = arrayOf(
            Note.COLUMN_NAME_ID,
            Note.COLUMN_NAME_TITLE,
            Note.COLUMN_NAME_DESCRIPTION,
            Note.COLUMN_NAME_DATE,
            Note.COLUMN_NAME_PRIVATE
        )

        val order = if (ascending) "ASC" else "DESC"  // "ASC" para ascendente, "DESC" para descendente
        val noteList: MutableList<Note> = mutableListOf()

        try {
            val cursor = db.query(
                Note.TABLE_NAME,   // La tabla de notas
                projection,        // Las columnas a devolver
                null,              // Sin condiciones (queremos todas las notas)
                null,              // Sin valores adicionales
                null,              // No agrupar filas
                null,              // No filtrar por grupos de filas
                "${Note.COLUMN_NAME_DATE} $order"  // Ordenar por fecha
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DESCRIPTION))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_DATE))
                val private = cursor.getInt(cursor.getColumnIndexOrThrow(Note.COLUMN_NAME_PRIVATE)) == 1

                val note = Note(id, title, description, date, private)
                noteList.add(note)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return noteList
    }

}
