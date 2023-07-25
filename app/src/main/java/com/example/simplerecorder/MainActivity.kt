package com.example.simplerecorder

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.simplerecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() {
        binding.startButton.setOnClickListener {
            Toast.makeText(applicationContext, "Start", Toast.LENGTH_SHORT).show()
            binding.textView.text = "Start"

            val intent = Intent(applicationContext, MyService::class.java)
            startService(intent)
        }

        binding.endButton.setOnClickListener {
            Toast.makeText(applicationContext, "End", Toast.LENGTH_SHORT).show()
            binding.textView.text = "End"

            val intent = Intent(applicationContext, MyService::class.java)
            stopService(intent)
        }
    }
}