package com.example.simplerecorder.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    private var startTimeStamp: Long = 0L
    private var duration: Long = 0L
    private val countUpAction: Runnable = object : Runnable {
        override fun run() {
            val currentTimeStamp = System.currentTimeMillis()
            val countTimeSeconds = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()
            updateCountTime(countTimeSeconds)
            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        startTimeStamp = System.currentTimeMillis()
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        duration = System.currentTimeMillis() - startTimeStamp
        handler?.removeCallbacks(countUpAction)
    }

    fun resumeCountUp() {
        startTimeStamp = System.currentTimeMillis() - duration
        handler?.post(countUpAction)
    }

    fun clearCountTime() {
        updateCountTime(0)
        stopCountUp()
    }

    private fun updateCountTime(countTimeSeconds: Int) {
        val minute = countTimeSeconds / 60
        val second = countTimeSeconds % 60
        text = "%02d:%02d".format(minute, second)
    }
}