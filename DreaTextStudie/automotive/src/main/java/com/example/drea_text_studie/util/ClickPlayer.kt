package com.example.drea_text_studie.util

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import com.example.drea_text_studie.R

class ClickPlayer(context: Context) {

    private var clickMediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.click)


    fun playClickSound() {
        val params =  PlaybackParams()
        params.speed = 1.0F
        clickMediaPlayer.playbackParams = params
        clickMediaPlayer.start()
    }

    fun destroy() {
        clickMediaPlayer.release()
    }
}