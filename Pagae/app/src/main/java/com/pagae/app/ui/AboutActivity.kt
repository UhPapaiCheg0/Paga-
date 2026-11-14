package com.pagae.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pagae.app.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = getString(R.string.about_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: ""
        } catch (e: Exception) {
            ""
        }
        findViewById<TextView>(R.id.txtVersion).text =
            if (versionName.isBlank()) "" else "Versão $versionName"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
