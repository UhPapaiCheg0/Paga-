package com.pagae.app.ui

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper
import com.pagae.app.db.TotalRow

class ManageBalancesActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: TotalsAdapter
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_balances)
        title = "Gerenciar saldos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)
        adapter = TotalsAdapter(emptyList()) { row -> onRowClick(row) }

        val rv = findViewById<RecyclerView>(R.id.rvTotals)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<EditText>(R.id.edtSearch).doAfterTextChanged {
            query = it.toString()
            refresh()
        }

        refresh()
    }

    private fun refresh() {
        adapter.update(db.totals(query))
    }

    private fun onRowClick(row: TotalRow) {
        val options = arrayOf("Subtrair um valor específico", "Registrar quitação do saldo")
        AlertDialog.Builder(this)
            .setTitle(row.name + " — " + formatMoney(row.total))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showSubtractDialog(row)
                    1 -> showClearAllDialog(row)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showSubtractDialog(row: TotalRow) {
        val input = EditText(this)
        input.hint = "Valor em reais (ex: 10,00)"
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        AlertDialog.Builder(this)
            .setTitle("Subtrair valor de " + row.name)
            .setView(input)
            .setPositiveButton("Confirmar") { _, _ ->
                val value = parseMoney(input.text.toString())
                if (value == null || value <= 0.0) {
                    toast("Valor inválido")
                } else {
                    db.payAmount(row.id, value, nowString())
                    toast("Subtraído " + formatMoney(value) + " de " + row.name)
                    refresh()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showClearAllDialog(row: TotalRow) {
        AlertDialog.Builder(this)
            .setTitle("Registrar quitação")
            .setMessage(row.name + " pagou o valor acumulado de " + formatMoney(row.total) + "?")
            .setPositiveButton("Sim") { _, _ ->
                db.clearProfile(row.id)
                toast("Quitação registrada para " + row.name + "; histórico preservado")
                refresh()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onSupportNavigateUp():Boolean {
        finish()
        return true
    }
}
