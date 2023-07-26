package com.example.simplerecorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplerecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() {
        binding.startButton.setOnClickListener {
            binding.textView.text = "Start"

            val intent = Intent(applicationContext, MyService::class.java)
            startService(intent)
        }

        binding.endButton.setOnClickListener {
            binding.textView.text = "End"

            val intent = Intent(applicationContext, MyService::class.java)
            stopService(intent)
        }

        // TODO: 삭제
        binding.button.setOnClickListener {
            val intent = Intent(applicationContext, RecordingActivity::class.java)
            startActivity(intent)
        }
    }
}