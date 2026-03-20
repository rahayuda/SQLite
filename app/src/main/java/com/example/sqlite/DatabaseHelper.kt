package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "storage", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // Insert data
    fun insertUser(name: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
        }
        return db.insert("users", null, values)
    }

    // Update data
    fun updateUser(id: Int, name: String, email: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    // Delete data
    fun deleteUser(id: Int): Int {
        val db = writableDatabase
        return db.delete("users", "id = ?", arrayOf(id.toString()))
    }

    // Fetch all users
    fun getAllUsers(): List<User> {
        val db = readableDatabase
        val users = mutableListOf<User>()
        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val email = cursor.getString(cursor.getColumnIndex("email"))
                users.add(User(id, name, email))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    // Inner class User
    data class User(val id: Int, val name: String, val email: String)
}
