package com.pagae.app.ui

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.pagae.app.R

class DisplaySettingsActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "pagae_display_preferences"
        const val KEY_MODE = "display_mode"
        const val KEY_APPLY_TO = "display_apply_to"
        const val MODE_LIST = "list"
        const val MODE_GRID = "grid"
        const val APPLY_FOODS = "foods"
        const val APPLY_PEOPLE = "people"
        const val APPLY_BOTH = "both"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_settings)
        title = "Configurações da tela"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val modeGroup = findViewById<RadioGroup>(R.id.groupDisplayMode)
        val applyGroup = findViewById<RadioGroup>(R.id.groupApplyTo)

        modeGroup.check(
            if (preferences.getString(KEY_MODE, MODE_LIST) == MODE_GRID) R.id.radioGrid
            else R.id.radioList
        )
        applyGroup.check(
            when (preferences.getString(KEY_APPLY_TO, APPLY_BOTH)) {
                APPLY_FOODS -> R.id.radioFoods
                APPLY_PEOPLE -> R.id.radioPeople
                else -> R.id.radioBoth
            }
        )

        findViewById<MaterialButton>(R.id.btnSaveDisplaySettings).setOnClickListener {
            val selectedMode = when (modeGroup.checkedRadioButtonId) {
                R.id.radioGrid -> MODE_GRID
                else -> MODE_LIST
            }
            val selectedTarget = when (applyGroup.checkedRadioButtonId) {
                R.id.radioFoods -> APPLY_FOODS
                R.id.radioPeople -> APPLY_PEOPLE
                else -> APPLY_BOTH
            }
            preferences.edit()
                .putString(KEY_MODE, selectedMode)
                .putString(KEY_APPLY_TO, selectedTarget)
                .apply()
            Toast.makeText(this, "Preferências salvas", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
