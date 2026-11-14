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
import com.pagae.app.db.Food

class EditFoodsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FoodEditAdapter
    private lateinit var fabDelete: FloatingActionButton
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_foods)
        title = "Editar produtos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)
        fabDelete = findViewById(R.id.fabDelete)
        adapter = FoodEditAdapter(emptyList()) { food -> onFoodClick(food) }

        val rv = findViewById<RecyclerView>(R.id.rvFoods)
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
        adapter.update(db.searchFoods(query))
    }

    private fun onFoodClick(food: Food) {
        if (adapter.selectionMode) {
            if (adapter.checked.contains(food.id)) adapter.checked.remove(food.id)
            else adapter.checked.add(food.id)
            adapter.notifyDataSetChanged()
            return
        }
        val options = arrayOf("Editar nome", "Editar valor", "Editar emoji", "Excluir")
        AlertDialog.Builder(this)
            .setTitle(food.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> promptText("Editar nome", food.name) { name ->
                        db.updateFood(food.id, name, food.price, food.emoji)
                        refresh()
                    }
                    1 -> promptValue("Editar valor", food.price) { price ->
                        db.updateFood(food.id, food.name, price, food.emoji)
                        refresh()
                    }
                    2 -> promptText("Editar emoji", food.emoji) { emoji ->
                        db.updateFood(food.id, food.name, food.price, emoji)
                        refresh()
                    }
                    3 -> enterSelectionMode(food)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enterSelectionMode(food: Food) {
        adapter.selectionMode = true
        adapter.checked.add(food.id)
        fabDelete.visibility = View.VISIBLE
    }

    private fun exitSelectionMode() {
        adapter.selectionMode = false
        fabDelete.visibility = View.GONE
    }

    private fun confirmDelete() {
        val count = adapter.checked.size
        if (count == 0) {
            toast("Selecione pelo menos uma comida")
            return
        }
        AlertDialog.Builder(this)
            .setMessage("Excluir $count comida(s)?")
            .setPositiveButton("Excluir") { _, _ ->
                db.deleteFoods(adapter.checked.toList())
                toast("$count comida(s) excluída(s)")
                exitSelectionMode()
                refresh()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun promptText(title: String, initial: String, onSave: (String) -> Unit) {
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

    private fun promptValue(title: String, initial: Double, onSave: (Double) -> Unit) {
        val input = EditText(this)
        input.setText(String.format(java.util.Locale("pt", "BR"), "%.2f", initial))
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val value = parseMoney(input.text.toString())
                if (value == null || value <= 0.0) toast("Valor inválido")
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
