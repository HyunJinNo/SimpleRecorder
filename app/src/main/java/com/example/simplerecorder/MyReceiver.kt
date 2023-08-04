package com.example.simplerecorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_RECORD = "ACTION_RECORD"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("SimpleRecorder", "화면 꺼짐")
            }
            Intent.ACTION_SCREEN_ON -> {
                Log.d("SimpleRecorder", "화면 켜짐")
            }
            ACTION_RECORD -> {
                Toast.makeText(context, "녹음 시작", Toast.LENGTH_SHORT).show()
            }
            ACTION_PAUSE -> {
                Toast.makeText(context, "녹음 일시 정지", Toast.LENGTH_SHORT).show()
            }
            ACTION_RESUME -> {
                Toast.makeText(context, "녹음 재개", Toast.LENGTH_SHORT).show()
            }
            ACTION_STOP -> {
                Toast.makeText(context, "녹음 종료", Toast.LENGTH_SHORT).show()
            }
            ACTION_CANCEL -> {
                Toast.makeText(context, "녹음 취소", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }
}