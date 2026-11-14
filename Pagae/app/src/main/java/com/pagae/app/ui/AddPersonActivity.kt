package com.pagae.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper

class AddPersonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)
        title = "Adicionar pessoa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = DatabaseHelper(this)
        val edtName = findViewById<TextInputEditText>(R.id.edtName)
        val edtContact = findViewById<TextInputEditText>(R.id.edtContact)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            val name = edtName.text.toString().trim()
            if (name.isBlank()) {
                toast("Digite o nome da pessoa")
                return@setOnClickListener
            }
            db.addPerson(name, edtContact.text.toString().trim())
            toast("Pessoa adicionada")
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
