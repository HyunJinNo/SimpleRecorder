package com.example.simplerecorder.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.simplerecorder.R
import com.example.simplerecorder.receivers.MyReceiver

object NotificationGenerator {
    private const val NOTIFICATION_ID = 9999
    private const val ANDROID_CHANNEL_ID = "com.example.simplerecorder"

    fun generateNotification(context: Context, layout: Int): Notification {
        // 안드로이드 Oreo 버전(API LEVEL 26)부터 Background 제약이 있기 때문에 Foreground 서비스를 실행해야함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification(상단 알림) 채널 생성
            val channel = NotificationChannel(
                ANDROID_CHANNEL_ID,
                "MyService",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }

            // Notification 서비스 객체를 가져옴.
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Get the layouts to use in the custom notification
        val notificationLayout = RemoteViews(context.packageName, layout).apply {
            setTextViewText(R.id.timeTextView, AudioTimer.getTimeStamp())
            setOnClickPendingIntent(
                R.id.stopButton,
                PendingIntent.getBroadcast(
                    context,
                    1000,
                    Intent(context, MyReceiver::class.java)
                        .apply {
                            action = MyReceiver.ACTION_STOP
                        },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            setOnClickPendingIntent(
                R.id.recordButton,
                PendingIntent.getBroadcast(
                    context,
                    1000,
                    Intent(context, MyReceiver::class.java)
                        .apply {
                            action = MyReceiver.ACTION_RECORD
                        },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            setOnClickPendingIntent(
                R.id.cancelButton,
                PendingIntent.getBroadcast(
                    context,
                    1000,
                    Intent(context, MyReceiver::class.java)
                        .apply {
                            action = MyReceiver.ACTION_CANCEL
                        },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        // Notification 알림 객체 반환
        return NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
            //.setContentTitle(getString(R.string.app_name))
            //.setContentText("SmartTracker Running")
            .setSmallIcon(R.drawable.baseline_mic_24)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .build()
    }

    fun notifyNotification(context: Context, layout: Int) {
        // Notification 서비스 객체를 가져옴.
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = generateNotification(context, layout)
        manager.notify(NOTIFICATION_ID, notification)
    }
}