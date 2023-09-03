package com.jar89.playlistmaker.sharing.domain.impl

import com.jar89.playlistmaker.sharing.domain.api.SharingInteractor
import com.jar89.playlistmaker.sharing.domain.api.SharingRepository

class SharingInteractorImpl(private val sharingRepository: SharingRepository): SharingInteractor {
    override fun shareApp() {
        sharingRepository.shareApp()
    }

    override fun contactSupport() {
        sharingRepository.contactSupport()
    }

    override fun openTerms() {
        sharingRepository.openTerms()
    }
}