package com.example.simplerecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecorder.databinding.ActivityRecordingBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RecordingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordingBinding
    private var recorder: MediaRecorder? = null
    private val recordingFilePath: String by lazy {
        "${Environment.getExternalStorageDirectory().absolutePath}/recording.3gp"
    }
    private var player: MediaPlayer? = null
    private var filename = ""

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

        if (!permissionToRecordAccepted) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AudioAdapter(arrayOf<String>("A", "B", "C"))
        binding.recyclerView.adapter = adapter

        initListeners()
        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder = null
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(filename)
                prepare()
                start()
            } catch (e: IOException) {
                Log.d("startPlaying()", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        val sdCard = Environment.getExternalStorageDirectory()

        val tempPath = sdCard.absolutePath + "/Simple Recorder"
        if (!File(tempPath).exists()) {
            File(tempPath).mkdirs()
        }

        val file = File(sdCard, "Simple Recorder/recorded.mp4")
        val filepath = file.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) // 오디오 소스 설정 (마이크)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 출력 파일 포맷 설정
            setOutputFile(filepath) // 출력 파일 이름 설정
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare() // 초기화 완료
            } catch (e: IOException) {
                Log.e("startRecoring()", "prepare() failed")
            }

            start()
        }

        /*
        recorder?.start() // 녹음 시작

        recorder?.stop() // 녹음 중지
        recorder?.reset() // You can reuse the object by going back to setAudioSource() step
        recorder?.release() // Now the object cannot be reused.
        */
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
    }

    private fun initListeners() {
        binding.recordButton.setOnClickListener {
            binding.textView2.text = "startRecording"
            startRecording()
        }
        binding.pauseButton.setOnClickListener {
            binding.textView2.text = "pause"
        }
        binding.stopButton.setOnClickListener {
            binding.textView2.text = "stopRecording"
            stopRecording()
        }
        binding.cancelButton.setOnClickListener {
            binding.textView2.text = "cancel"
        }
    }
}