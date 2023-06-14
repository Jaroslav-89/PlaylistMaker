package com.jar89.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareBtn = findViewById<FrameLayout>(R.id.settingsShareBtn)
        val supportBtn = findViewById<FrameLayout>(R.id.settingsSupportBtn)
        val userAgreementBtn = findViewById<FrameLayout>(R.id.settingsUserAgreementBtn)
        val backBtn = findViewById<ImageView>(R.id.settingsBackBtn)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        shareBtn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumUrl))
            startActivity(shareIntent)
        }

        supportBtn.setOnClickListener {
            val email = getString(R.string.emailSupport)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSupportSubject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.emailSupportMessage))
            startActivity(supportIntent)
        }

        userAgreementBtn.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW)
            userAgreementIntent.data = Uri.parse(getString(R.string.userAgreementUrl))
            startActivity(userAgreementIntent)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }
}