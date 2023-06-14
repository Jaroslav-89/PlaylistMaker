package com.jar89.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareBtn = findViewById<FrameLayout>(R.id.settings_share_btn)
        val supportBtn = findViewById<FrameLayout>(R.id.settings_support_btn)
        val userAgreementBtn = findViewById<FrameLayout>(R.id.settings_user_agreement_btn)
        val backBtn = findViewById<ImageView>(R.id.settings_back_btn)

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