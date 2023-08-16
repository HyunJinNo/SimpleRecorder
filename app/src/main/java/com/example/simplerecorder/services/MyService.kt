package com.example.simplerecorder.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.IBinder
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.simplerecorder.receivers.MyReceiver
import com.example.simplerecorder.utils.NotificationGenerator
import com.example.simplerecorder.R
import com.example.simplerecorder.utils.AudioRecorder
import com.example.simplerecorder.utils.AudioTimer
import com.example.simplerecorder.utils.RecordingState

class MyService : Service() {
    private var myReceiver: MyReceiver? = null

    companion object {
        private const val NOTIFICATION_ID = 9999
    }

    // 서비스가 최소 생성될 때 callback 함수
    override fun onCreate() {
        super.onCreate()

        // 브로드캐스트 리시버가 null 인 경우에만 실행
        if (myReceiver == null) {
            myReceiver = MyReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_TIME_TICK)
            }
            registerReceiver(myReceiver, filter)
        }
    }

    // 서비스를 호출하는 클라이언트가 startService() 함수를 호출할 떄마다 불리는 callback 함수
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Toast.makeText(applicationContext, "서비스 시작", Toast.LENGTH_SHORT).show()

        intent?.let {
            if (it.action == null) {
                // 서비스가 최초 실행이 아닌 경우 onCreate 가 불리지 않을 수 있음.
                if (myReceiver == null) {
                    myReceiver = MyReceiver()
                    val filter = IntentFilter().apply {
                        addAction(Intent.ACTION_SCREEN_ON)
                        addAction(Intent.ACTION_TIME_TICK)
                    }
                    registerReceiver(myReceiver, filter)
                }
            }
        }

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val mode = prefs.getBoolean("foregroundType", false)

        if (mode) {
            val notification = NotificationGenerator.generateNotification(
                this, R.layout.custom_notification2
            )
            startForeground(NOTIFICATION_ID, notification)
        } else {
            val notification = NotificationGenerator.generateNotification(
                this, R.layout.custom_notification
            )
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(applicationContext, "서비스 종료", Toast.LENGTH_SHORT).show()

        if (AudioRecorder.recordingState != RecordingState.BEFORE_RECORDING) {
            Toast.makeText(this, "녹음 종료", Toast.LENGTH_SHORT).show()
            AudioTimer.stopTimer()
            AudioRecorder.stopRecording()
        }

        // 서비스가 종료될 때 브로드캐스트 리시버 등록도 해제
        myReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}