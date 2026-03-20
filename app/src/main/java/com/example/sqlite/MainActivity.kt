package com.example.sqlite

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var adapter: ArrayAdapter<String>
    private val userList = mutableListOf<String>()
    private val userMap = mutableListOf<DatabaseHelper.User>()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        listView = findViewById(R.id.list)
        swipe = findViewById(R.id.swipe)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        listView.adapter = adapter

        swipe.setOnRefreshListener {
            fetchUsers()
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showUserForm(null)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            showUserForm(userMap[position])
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val user = userMap[position]
            AlertDialog.Builder(this)
                .setTitle("Hapus Data")
                .setMessage("Hapus ${user.name}?")
                .setPositiveButton("Hapus") { _, _ -> deleteUser(user.id) }
                .setNegativeButton("Batal", null)
                .show()
            true
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        swipe.isRefreshing = true
        val users = dbHelper.getAllUsers()
        userMap.clear()
        userList.clear()
        users.forEach {
            userMap.add(it)
            userList.add("${it.id}. ${it.name}, ${it.email}")
        }
        adapter.notifyDataSetChanged()
        swipe.isRefreshing = false
    }

    private fun showUserForm(user: DatabaseHelper.User?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.form, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)

        if (user != null) {
            editName.setText(user.name)
            editEmail.setText(user.email)
        }

        AlertDialog.Builder(this)
            .setTitle(if (user == null) "Tambah User" else "Edit User")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val name = editName.text.toString()
                val email = editEmail.text.toString()
                if (user == null) {
                    insertUser(name, email)
                } else {
                    updateUser(user.id, name, email)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun insertUser(name: String, email: String) {
        dbHelper.insertUser(name, email)
        fetchUsers()
    }

    private fun updateUser(id: Int, name: String, email: String) {
        dbHelper.updateUser(id, name, email)
        fetchUsers()
    }

    private fun deleteUser(id: Int) {
        dbHelper.deleteUser(id)
        fetchUsers()
    }
}
