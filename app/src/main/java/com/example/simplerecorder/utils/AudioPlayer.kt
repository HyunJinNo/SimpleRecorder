package com.example.simplerecorder.utils

import android.media.MediaPlayer
import android.util.Log
import android.widget.MediaController
import java.io.IOException

object AudioPlayer : MediaController.MediaPlayerControl {
    var mediaPlayer: MediaPlayer? = null
    private var filepath: String = ""

    fun ready(path: String) {
        filepath = path
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filepath)
                prepare()
            } catch (e: IOException) {
                Log.d("AudioPlayer", "prepare() failed")
            }
        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun getDuration() = mediaPlayer?.duration ?: 0

    override fun getCurrentPosition() = mediaPlayer?.currentPosition ?: 0

    override fun seekTo(pos: Int) {
        mediaPlayer?.seekTo(pos)
    }

    override fun isPlaying() = mediaPlayer?.isPlaying ?: false

    override fun getBufferPercentage() = 0

    override fun canPause() = true

    override fun canSeekBackward() = true

    override fun canSeekForward() = true

    override fun getAudioSessionId() = 0
}