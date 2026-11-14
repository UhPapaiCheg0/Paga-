package com.pagae.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper

class TotalsFragment : Fragment(R.layout.fragment_totals) {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: TotalsAdapter
    private var query = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = DatabaseHelper(requireContext())
        adapter = TotalsAdapter(emptyList()) { row ->
            val intent = Intent(context, HistoryActivity::class.java)
            intent.putExtra("profile_id", row.id)
            intent.putExtra("profile_name", row.name)
            startActivity(intent)
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvTotals)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        view.findViewById<EditText>(R.id.edtSearch).doAfterTextChanged {
            query = it.toString()
            refresh()
        }

        view.findViewById<Button>(R.id.btnManage).setOnClickListener {
            startActivity(Intent(context, ManageBalancesActivity::class.java))
        }

        refresh()
        refreshReport()
    }

    override fun onResume() {
        super.onResume()
        refresh()
        refreshReport()
    }

    private fun refresh() {
        adapter.update(db.totals(query))
    }

    private fun refreshReport() {
        val summary = db.reportSummary()
        view?.findViewById<TextView>(R.id.txtReportDebt)?.text = formatMoney(summary.currentDebt)
        view?.findViewById<TextView>(R.id.txtReportSales)?.text = formatMoney(summary.salesTotal)
        view?.findViewById<TextView>(R.id.txtReportPayments)?.text = formatMoney(summary.paymentsTotal)
        view?.findViewById<MonthlySalesChartView>(R.id.monthlyChart)?.submitData(db.monthlySales())
    }
}
