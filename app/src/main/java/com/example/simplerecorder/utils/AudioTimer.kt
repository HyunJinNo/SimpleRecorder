package com.example.simplerecorder.utils

import java.util.Timer
import java.util.TimerTask

object AudioTimer {
    private var startTimeStamp: Long = 0L
    private var duration: Long = 0L
    private var timer = Timer()

    fun startTimer(timerTask: TimerTask) {
        startTimeStamp = System.currentTimeMillis()
        duration = 0L
        timer = Timer()
        timer.schedule(timerTask, 1L, 1000L)
    }

    fun pauseTimer() {
        duration = System.currentTimeMillis() - startTimeStamp
        timer.cancel()
    }

    fun resumeTimer(timerTask: TimerTask) {
        startTimeStamp = System.currentTimeMillis() - duration
        timer = Timer()
        timer.schedule(timerTask, 1L, 1000L)
    }

    fun stopTimer() {
        startTimeStamp = 0L
        duration = 0L
        timer.cancel()
    }

    fun getTimeStamp(): String {
        if (startTimeStamp == 0L) {
            return "00:00:00"
        }

        val currentTimeStamp = System.currentTimeMillis()
        val countTimeSeconds = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()
        val hour = countTimeSeconds / 3600
        val minute = (countTimeSeconds % 3600) / 60
        val second = countTimeSeconds % 60
        return "%02d:%02d:%02d".format(hour, minute, second)
    }
}