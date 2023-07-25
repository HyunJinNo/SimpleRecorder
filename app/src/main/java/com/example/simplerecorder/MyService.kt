package com.example.simplerecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast

class MyService : Service() {
    private var myReceiver: MyReceiver? = null

    private val ANDROID_CHANNEL_ID = "com.example.simplerecorder"
    private val NOTIFICATION_ID = 9999

    // 서비스가 최소 생성될 때 callback 함수
    override fun onCreate() {
        super.onCreate()

        // 브로드캐스트 리시버가 null 인 경우에만 실행
        if (myReceiver == null) {
            myReceiver = MyReceiver()
            val filter = IntentFilter()
            with (filter) {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
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
                    val filter = IntentFilter()
                    with (filter) {
                        addAction(Intent.ACTION_SCREEN_OFF)
                        addAction(Intent.ACTION_SCREEN_ON)
                    }
                    registerReceiver(myReceiver, filter)
                }
            }
        }

        // 안드로이드 Oreo 버전(API LEVEL 26)부터 Background 제약이 있기 때문에 Foreground 서비스를 실행해야함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification(상단 알림) 채널 생성
            val channel = NotificationChannel(
                ANDROID_CHANNEL_ID,
                "MyService",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }

            // Notification 서비스 객체를 가져옴.
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            // Notification 알림 객체 생성
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("SmartTracker Runnning")
            val notification = builder.build()

            // Notification 알림과 함께 Foreground 서비스 시작
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(applicationContext, "서비스 종료", Toast.LENGTH_SHORT).show()

        // 서비스가 종료될 때 브로드캐스트 리시버 등록도 해제
        myReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}