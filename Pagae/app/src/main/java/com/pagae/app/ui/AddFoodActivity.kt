package com.pagae.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper

class AddFoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        title = "Adicionar produto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = DatabaseHelper(this)
        val edtName = findViewById<TextInputEditText>(R.id.edtName)
        val edtPrice = findViewById<TextInputEditText>(R.id.edtPrice)
        val edtEmoji = findViewById<TextInputEditText>(R.id.edtEmoji)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            val name = edtName.text.toString().trim()
            val price = parseMoney(edtPrice.text.toString())
            when {
                name.isBlank() -> toast("Digite o nome da comida")
                price == null || price <= 0.0 -> toast("Digite um valor válido em reais")
                else -> {
                    db.addFood(name, price, edtEmoji.text.toString().trim())
                    toast("Comida adicionada")
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
