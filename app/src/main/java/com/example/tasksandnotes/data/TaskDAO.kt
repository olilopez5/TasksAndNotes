package com.example.tasksandnotes.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.tasksandnotes.utils.DatabaseManager


class TaskDAO(context: Context) {

    val databaseManager = DatabaseManager(context)

    fun insert(task: Task){
        //data in write mode
        val db = databaseManager.writableDatabase
        //new map of values column name is key
        val values = ContentValues().apply {
            put(Task.COLUMN_NAME_TITLE,task.title)
            put(Task.COLUMN_NAME_DONE,task.done)
            //put(Task.COLUMN_NAME_PRIORITY,task.priority)
        }
        // new row, return PK od new row (insert ? error)
        try {
            val newRowId = db.insert(Task.TABLE_NAME, null,values)

            Log.i("DATABASE", "Insert : $newRowId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

    }
    fun update(task: Task){

        val db = databaseManager.writableDatabase

        val values = ContentValues().apply {
            put(Task.COLUMN_NAME_TITLE,task.title)
            put(Task.COLUMN_NAME_DONE,task.done)
        }

        try {
            //  whereClause = ? , whereArgs arrayOf("${task.id}")
            val newRowId = db.update(Task.TABLE_NAME, values, "${Task.COLUMN_NAME_ID} = ${task.id} ", null)

            Log.i("DATABASE", "Update task: ${task.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

    }
    fun delete(task: Task){

        val db = databaseManager.writableDatabase

        //
        try {
            val deleteRows = db.delete(Task.TABLE_NAME,"${Task.COLUMN_NAME_ID} = ${task.id}", null)

            Log.i("DATABASE", "Delete task: ${task.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
    fun findById(id: Long): Task?{
        val db = databaseManager.readableDatabase

        val projection = arrayOf(
            Task.COLUMN_NAME_ID,
            Task.COLUMN_NAME_TITLE,
            Task.COLUMN_NAME_DONE)

        val selection = "${Task.COLUMN_NAME_ID} = $id"

        var task: Task? = null

        try {
            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
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
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0

                task = Task(id, title, done)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return task
    }

    fun findAll(): List<Task> {
        val db = databaseManager.readableDatabase

        val projection = arrayOf(
            Task.COLUMN_NAME_ID,
            Task.COLUMN_NAME_TITLE,
            Task.COLUMN_NAME_DONE
        )

        var taskList: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                Task.COLUMN_NAME_DONE             // The sort order
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0

                val task = Task(id, title, done)
                taskList.add(task)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return taskList
    }

    fun countByNotDone(): Int {
        val db = databaseManager.readableDatabase

        val projection = arrayOf("COUNT(*)")
        val selection = "${Task.COLUMN_NAME_DONE} = false"

        try {
            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
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
                return cursor.getInt(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return 0
    }
}