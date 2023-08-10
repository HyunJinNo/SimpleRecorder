package com.example.simplerecorder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.example.simplerecorder.utils.AudioRecorder
import com.example.simplerecorder.utils.NotificationGenerator
import com.example.simplerecorder.R
import com.example.simplerecorder.utils.AudioTimer
import com.example.simplerecorder.utils.RecordingState
import java.util.TimerTask
import java.util.concurrent.atomic.AtomicInteger

class MyReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_RECORD = "ACTION_RECORD"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }

    private var count = AtomicInteger()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIME_TICK -> {
                count.set(0)
                Log.i("SimpleRecorder", "count 리셋: ${count.get()}")
            }
            Intent.ACTION_SCREEN_OFF -> {
                val num = count.incrementAndGet()
                Log.i("SimpleRecorder", "count: ${count.get()}")
                if (num == 10) {
                    startRecording(context)
                }
            }
            Intent.ACTION_SCREEN_ON -> {
                val num = count.incrementAndGet()
                Log.i("SimpleRecorder", "count: ${count.get()}")
                if (num == 10) {
                    startRecording(context)
                }
            }
            ACTION_RECORD -> {
                when (AudioRecorder.recordingState) {
                    RecordingState.BEFORE_RECORDING -> {
                        Toast.makeText(context, "녹음 시작", Toast.LENGTH_SHORT).show()
                        AudioTimer.startTimer(object : TimerTask() {
                            override fun run() {
                                NotificationGenerator.notifyNotification(
                                    context,
                                    R.layout.custom_notification_recording
                                )
                            }
                        })
                        AudioRecorder.startRecording()
                    }
                    RecordingState.ON_RECORDING -> {
                        Toast.makeText(context, "녹음 일시 정지", Toast.LENGTH_SHORT).show()
                        NotificationGenerator.notifyNotification(
                            context,
                            R.layout.custom_notification_pause
                        )
                        AudioTimer.pauseTimer()
                        AudioRecorder.pauseRecording()
                    }
                    RecordingState.PAUSE -> {
                        Toast.makeText(context, "녹음 재개", Toast.LENGTH_SHORT).show()
                        AudioTimer.resumeTimer(object : TimerTask() {
                            override fun run() {
                                NotificationGenerator.notifyNotification(
                                    context,
                                    R.layout.custom_notification_recording
                                )
                            }
                        })
                        AudioRecorder.resumeRecording()
                    }
                }
            }
            ACTION_STOP -> {
                Toast.makeText(context, "녹음 종료", Toast.LENGTH_SHORT).show()
                AudioTimer.stopTimer()
                NotificationGenerator.notifyNotification(context, R.layout.custom_notification)
                AudioRecorder.stopRecording()
            }
            ACTION_CANCEL -> {
                Toast.makeText(context, "녹음 취소", Toast.LENGTH_SHORT).show()
                AudioTimer.stopTimer()
                NotificationGenerator.notifyNotification(context, R.layout.custom_notification)
                AudioRecorder.cancelRecording()
            }
            else -> {
                // Do nothing.
            }
        }
    }

    private fun startRecording(context: Context) {
        if (AudioRecorder.recordingState == RecordingState.BEFORE_RECORDING) {
            AudioTimer.startTimer(object : TimerTask() {
                override fun run() {
                    NotificationGenerator.notifyNotification(
                        context,
                        R.layout.custom_notification_recording
                    )
                }
            })
            AudioRecorder.startRecording()
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(50L)
        }
    }
}