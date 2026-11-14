package com.pagae.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val profileId = intent.getLongExtra("profile_id", -1L)
        val profileName = intent.getStringExtra("profile_name") ?: "Histórico"
        title = profileName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = DatabaseHelper(this)
        val adapter = HistoryAdapter(db.history(profileId))

        val total = db.totals("").find { it.id == profileId }?.total ?: 0.0
        findViewById<TextView>(R.id.txtTotal).text = "Total: " + formatMoney(total)

        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
