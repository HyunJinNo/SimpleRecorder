package com.example.simplerecorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("SimpleRecorder", "화면 꺼짐")
            }
            Intent.ACTION_SCREEN_ON -> {
                Log.d("SimpleRecorder", "화면 켜짐")
            }
            else -> {

            }
        }
    }
}