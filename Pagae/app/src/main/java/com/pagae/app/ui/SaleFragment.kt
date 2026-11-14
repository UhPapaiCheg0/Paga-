package com.pagae.app.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper

class SaleFragment : Fragment(R.layout.fragment_sale) {

    private lateinit var db: DatabaseHelper
    private lateinit var peopleAdapter: PersonPickAdapter
    private lateinit var foodAdapter: FoodPickAdapter
    private lateinit var txtSelectedPerson: TextView
    private lateinit var txtSelectedFood: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = DatabaseHelper(requireContext())
        txtSelectedPerson = view.findViewById(R.id.txtSelectedPerson)
        txtSelectedFood = view.findViewById(R.id.txtSelectedFood)

        view.findViewById<EditText>(R.id.edtSearchPerson)
            .doAfterTextChanged { peopleAdapter.update(db.searchPeople(it.toString())) }
        view.findViewById<EditText>(R.id.edtSearchFood)
            .doAfterTextChanged { foodAdapter.update(db.searchFoods(it.toString())) }

        applyDisplayPreferences()

        view.findViewById<FloatingActionButton>(R.id.fabSave).setOnClickListener { save() }
        view.findViewById<android.widget.ImageButton>(R.id.btnSaleSettings).setOnClickListener {
            startActivity(Intent(requireContext(), DisplaySettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (view != null && ::db.isInitialized) applyDisplayPreferences()
    }

    private fun applyDisplayPreferences() {
        val root = view ?: return
        val preferences = requireContext().getSharedPreferences(DisplaySettingsActivity.PREFS_NAME, 0)
        val gridMode = preferences.getString(DisplaySettingsActivity.KEY_MODE, DisplaySettingsActivity.MODE_LIST) == DisplaySettingsActivity.MODE_GRID
        val applyTo = preferences.getString(DisplaySettingsActivity.KEY_APPLY_TO, DisplaySettingsActivity.APPLY_BOTH)
        val gridPeople = gridMode && (applyTo == DisplaySettingsActivity.APPLY_PEOPLE || applyTo == DisplaySettingsActivity.APPLY_BOTH)
        val gridFoods = gridMode && (applyTo == DisplaySettingsActivity.APPLY_FOODS || applyTo == DisplaySettingsActivity.APPLY_BOTH)

        peopleAdapter = PersonPickAdapter(emptyList(), gridPeople) { updateSelectionText() }
        foodAdapter = FoodPickAdapter(emptyList(), gridFoods) { updateSelectionText() }

        root.findViewById<RecyclerView>(R.id.rvPeople).apply {
            layoutManager = if (gridPeople) GridLayoutManager(context, 3) else LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = peopleAdapter
        }
        root.findViewById<RecyclerView>(R.id.rvFoods).apply {
            layoutManager = if (gridFoods) GridLayoutManager(context, 3) else LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = foodAdapter
        }
        peopleAdapter.update(db.searchPeople(""))
        foodAdapter.update(db.searchFoods(""))
        updateSelectionText()
    }

    private fun updateSelectionText() {
        val p = peopleAdapter.selectedPerson()
        val f = foodAdapter.selectedFood()
        txtSelectedPerson.text = "Perfil selecionado: " + (p?.name ?: "nenhum")
        txtSelectedFood.text = "Comida selecionada: " +
            (f?.let { if (it.emoji.isBlank()) it.name else it.emoji + " " + it.name } ?: "nenhuma")
    }

    private fun save() {
        val p = peopleAdapter.selectedPerson()
        val f = foodAdapter.selectedFood()
        when {
            p == null -> toast("Selecione um perfil")
            f == null -> toast("Selecione uma comida")
            else -> {
                db.addSale(p.id, f.name, f.price, nowString())
                toast("Venda salva: " + f.name + " para " + p.name + " (" + formatMoney(f.price) + ")")
                peopleAdapter.update(db.searchPeople(""))
                foodAdapter.update(db.searchFoods(""))
                updateSelectionText()
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}
