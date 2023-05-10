package com.jar89.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val backBtn = findViewById<ImageView>(R.id.settings_back_btn)

        backBtn.setOnClickListener {
//            val backIntent = Intent(this, MainActivity::class.java)
//            startActivity(backIntent)
            finish()
        }
    }
}