package com.example.simplerecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecorder.databinding.ActivityRecordingBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RecordingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordingBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var filepath: String = ""
    private var timeStamp: String = ""

    // Requesting permissions
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val dataList = mutableListOf<Array<String>>()

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
        binding.recyclerView.adapter = AudioAdapter(dataList)

        initListeners()
        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onStart() {
        super.onStart()
        dataList.clear()

        if (isExternalStorageReadable()) {
            val dir = Environment.getExternalStorageDirectory().absolutePath + "/Simple Recorder"
            if (!File(dir).exists()) {
                File(dir).mkdirs()
            }

            File(dir).listFiles()?.forEach {
                val filename = it.absolutePath.split("/").last().split(".").first()
                dataList.add(arrayOf(it.absolutePath, filename, "??:??:??", "2023-07-31"))
                binding.recyclerView.adapter?.notifyItemInserted(dataList.lastIndex)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder = null
        player = null
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
                setDataSource(filepath)
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
        if (!isExternalStorageWritable()) {
            Toast.makeText(applicationContext, "외부 저장소에 접근하는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val sdCard = Environment.getExternalStorageDirectory()
        val dir = sdCard.absolutePath + "/Simple Recorder"
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }

        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        filepath = File(sdCard, "Simple Recorder/${timeStamp}.m4a").absolutePath

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
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()

            val filename = filepath.split("/").last().split(".").first()
            val date = "${timeStamp.slice(0..3)}-${timeStamp.slice(4..5)}-${timeStamp.slice(6..7)}"
            dataList.add(arrayOf(filepath, filename, "??:??:??", date))
            binding.recyclerView.adapter?.notifyItemInserted(dataList.lastIndex)
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

    // Checks if a volume containing external storage is available for read and write.
    private fun isExternalStorageWritable() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    // Checks if a volume containing external storage is available to at least read.
    private fun isExternalStorageReadable() = Environment.getExternalStorageState() in setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}