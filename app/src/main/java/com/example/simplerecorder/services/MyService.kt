package com.example.simplerecorder.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast
import com.example.simplerecorder.receivers.MyReceiver
import com.example.simplerecorder.utils.NotificationGenerator
import com.example.simplerecorder.R

class MyService : Service() {
    private var myReceiver: MyReceiver? = null

    // 서비스가 최소 생성될 때 callback 함수
    override fun onCreate() {
        super.onCreate()

        // 브로드캐스트 리시버가 null 인 경우에만 실행
        if (myReceiver == null) {
            myReceiver = MyReceiver()
            val filter = IntentFilter().apply {
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
                    val filter = IntentFilter().apply {
                        addAction(Intent.ACTION_SCREEN_OFF)
                        addAction(Intent.ACTION_SCREEN_ON)
                    }
                    registerReceiver(myReceiver, filter)
                }
            }
        }

        NotificationGenerator.generateNotification(applicationContext, R.layout.custom_notification)

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