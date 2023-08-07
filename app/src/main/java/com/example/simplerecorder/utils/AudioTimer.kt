package com.example.simplerecorder.utils

import java.util.Timer
import java.util.TimerTask

object AudioTimer {
    var duration: Long = 0L
    private var timer = Timer()

    fun startTimer(timerTask: TimerTask) {
        duration = 0L
        timer = Timer()
        timer.schedule(timerTask, 1L, 1000L)
    }

    fun pauseTimer() {
        timer.cancel()
    }

    fun resumeTimer(timerTask: TimerTask) {
        timer = Timer()
        timer.schedule(timerTask, 1L, 1000L)
    }

    fun stopTimer() {
        duration = 0L
        timer.cancel()
    }

    fun getTimeStamp(): String {
        val minute = duration / 60
        val second = duration % 60
        return "%02d:%02d".format(minute, second)
    }
}