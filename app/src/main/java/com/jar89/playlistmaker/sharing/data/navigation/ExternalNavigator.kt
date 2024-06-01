package com.jar89.playlistmaker.sharing.data.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jar89.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigator(private val context: Context) {
    fun shareLink(url: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    fun openLink(url: String) {
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    fun openEmail(emailData: EmailData) {
        Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailTo))
            putExtra(Intent.EXTRA_SUBJECT, emailData.emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailData.emailText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    fun sharePlaylist(playlistDesc: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, playlistDesc)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }
}