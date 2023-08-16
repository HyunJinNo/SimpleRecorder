package com.example.simplerecorder.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.simplerecorder.services.MyService
import com.example.simplerecorder.R
import com.example.simplerecorder.databinding.SettingsActivityBinding
import com.example.simplerecorder.utils.AudioRecorder
import com.example.simplerecorder.utils.AudioTimer
import com.example.simplerecorder.utils.NotificationGenerator
import com.example.simplerecorder.utils.RecordingState

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "foregroundSetting" -> {
                    val flag = prefs.getBoolean("foregroundSetting", false)
                    val intent = Intent(applicationContext, MyService::class.java)
                    if (flag) {
                        startService(intent)
                    } else {
                        stopService(intent)
                    }
                }
                "foregroundType" -> {
                    if (AudioRecorder.recordingState != RecordingState.BEFORE_RECORDING) {
                        Toast.makeText(this, "녹음 종료", Toast.LENGTH_SHORT).show()
                        AudioTimer.stopTimer()
                        AudioRecorder.stopRecording()
                    }

                    val mode = prefs.getBoolean("foregroundType", false)
                    if (mode) {
                        NotificationGenerator.notifyNotification(
                            this, R.layout.custom_notification2
                        )
                    } else {
                        NotificationGenerator.notifyNotification(
                            this, R.layout.custom_notification
                        )
                    }
                }
                else -> {}
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}