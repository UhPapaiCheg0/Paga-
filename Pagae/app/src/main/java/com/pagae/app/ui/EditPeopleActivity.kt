package com.pagae.app.ui

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper
import com.pagae.app.db.Person

class EditPeopleActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: PersonEditAdapter
    private lateinit var fabDelete: FloatingActionButton
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_people)
        title = "Editar pessoas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)
        fabDelete = findViewById(R.id.fabDelete)
        adapter = PersonEditAdapter(emptyList()) { person -> onPersonClick(person) }

        val rv = findViewById<RecyclerView>(R.id.rvPeople)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<EditText>(R.id.edtSearch).doAfterTextChanged {
            query = it.toString()
            refresh()
        }

        fabDelete.setOnClickListener { confirmDelete() }

        refresh()
    }

    private fun refresh() {
        adapter.update(db.searchPeople(query))
    }

    private fun onPersonClick(person: Person) {
        if (adapter.selectionMode) {
            // No modo de exclusão, clicar no item marca/desmarca a caixa.
            if (adapter.checked.contains(person.id)) adapter.checked.remove(person.id)
            else adapter.checked.add(person.id)
            adapter.notifyDataSetChanged()
            return
        }
        val options = arrayOf("Editar nome", "Editar contato", "Excluir")
        AlertDialog.Builder(this)
            .setTitle(person.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> prompt("Editar nome", person.name) { newName ->
                        db.updatePersonName(person.id, newName)
                        refresh()
                    }
                    1 -> prompt("Editar contato", person.contact) { newContact ->
                        db.updatePersonContact(person.id, newContact)
                        refresh()
                    }
                    2 -> enterSelectionMode(person)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enterSelectionMode(person: Person) {
        adapter.selectionMode = true
        adapter.checked.add(person.id)
        fabDelete.visibility = View.VISIBLE
    }

    private fun exitSelectionMode() {
        adapter.selectionMode = false
        fabDelete.visibility = View.GONE
    }

    private fun confirmDelete() {
        val count = adapter.checked.size
        if (count == 0) {
            toast("Selecione pelo menos uma pessoa")
            return
        }
        AlertDialog.Builder(this)
            .setMessage("Excluir $count pessoa(s)? O histórico de compras também será apagado.")
            .setPositiveButton("Excluir") { _, _ ->
                db.deletePersons(adapter.checked.toList())
                toast("$count pessoa(s) excluída(s)")
                exitSelectionMode()
                refresh()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun prompt(title: String, initial: String, onSave: (String) -> Unit) {
        val input = EditText(this)
        input.setText(initial)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val value = input.text.toString().trim()
                if (value.isBlank()) toast("O campo não pode ficar vazio")
                else onSave(value)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
