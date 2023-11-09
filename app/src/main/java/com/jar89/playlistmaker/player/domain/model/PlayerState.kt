package com.jar89.playlistmaker.player.domain.model

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonIsPlay: Boolean,
    val progress: Int
) {

    class Default : PlayerState(false, true, 0)

    class Prepared : PlayerState(true, true, 0)

    class Playing(progress: Int) : PlayerState(true, false, progress)

    class Paused(progress: Int) : PlayerState(true, true, progress)
}