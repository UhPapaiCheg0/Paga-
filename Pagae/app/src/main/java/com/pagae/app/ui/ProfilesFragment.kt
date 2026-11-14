package com.pagae.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.pagae.app.R

class ProfilesFragment : Fragment(R.layout.fragment_profiles) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btnAddPerson).setOnClickListener {
            startActivity(Intent(context, AddPersonActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnEditPeople).setOnClickListener {
            startActivity(Intent(context, EditPeopleActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnAddFood).setOnClickListener {
            startActivity(Intent(context, AddFoodActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnEditFoods).setOnClickListener {
            startActivity(Intent(context, EditFoodsActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnAbout).setOnClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}
