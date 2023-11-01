package com.jar89.playlistmaker.player.ui.view_model

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonIsPlay: Boolean,
    val progress: String
) {

    class Default : PlayerState(false, true, "00:00")

    class Prepared : PlayerState(true, true, "00:00")

    class Playing(progress: String) : PlayerState(true, false, progress)

    class Paused(progress: String) : PlayerState(true, true, progress)
}