package com.jar89.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jar89.playlistmaker.App
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        binding.settingsShareBtn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumUrl))
            startActivity(shareIntent)
        }

        binding.settingsSupportBtn.setOnClickListener {
            val email = getString(R.string.emailSupport)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSupportSubject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.emailSupportMessage))
            startActivity(supportIntent)
        }

        binding.settingsUserAgreementBtn.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW)
            userAgreementIntent.data = Uri.parse(getString(R.string.userAgreementUrl))
            startActivity(userAgreementIntent)
        }

        binding.settingsBackBtn.setOnClickListener {
            finish()
        }
    }
}