package com.example.simplerecorder

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.simplerecorder.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "soundQuality" -> {
                    // TODO
                }
                "foregroundSetting" -> {
                    val flag = prefs.getBoolean("foregroundSetting", false)
                    val intent = Intent(applicationContext, MyService::class.java)
                    if (flag) {
                        startService(intent)
                    } else {
                        stopService(intent)
                    }
                }
                else -> {}
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            }
        }
    }
}