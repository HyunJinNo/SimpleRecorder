package com.example.simplerecorder

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.example.simplerecorder.databinding.ActivityRecordingBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RecordingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordingBinding
    private var recorder: MediaRecorder? = null
    private var filepath: String = ""
    private var recordingState = RecordingState.BEFORE_RECORDING

    // Requesting permissions
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        private const val REQUEST_PERMISSIONS = 200
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_PERMISSIONS) {
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

        setSupportActionBar(binding.toolbar)

        initListeners()
        requestPermissions(permissions, REQUEST_PERMISSIONS)
        binding.soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder = null
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

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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

        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext, R.drawable.baseline_pause_24))
        binding.stopButton.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.VISIBLE
        binding.countUpView.startCountUp()
        binding.soundVisualizerView.startVisualizing(isReplaying = false)
        recordingState = RecordingState.ON_RECORDING
    }

    private fun resumeRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext, R.drawable.baseline_pause_24))
        recorder?.resume()
        binding.countUpView.resumeCountUp()
        binding.soundVisualizerView.resumeVisualizing()
        recordingState = RecordingState.ON_RECORDING
    }

    private fun pauseRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext, R.drawable.baseline_fiber_manual_record_24))
        recorder?.pause()
        binding.countUpView.stopCountUp()
        binding.soundVisualizerView.stopVisualizing()
        recordingState = RecordingState.PAUSE
    }

    private fun stopRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext, R.drawable.baseline_fiber_manual_record_24))
        binding.stopButton.visibility = View.INVISIBLE
        binding.cancelButton.visibility = View.INVISIBLE

        recorder?.run {
            stop()
            reset()
            release()
        }
        binding.countUpView.clearCountTime()
        binding.soundVisualizerView.clearVisualizing()
        recordingState = RecordingState.BEFORE_RECORDING
        recorder = null
        Toast.makeText(applicationContext, "녹음 파일이 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun cancelRecording() {
        stopRecording()
        File(filepath).delete()
        Toast.makeText(applicationContext, "녹음이 취소되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun initListeners() {
        binding.recordButton.setOnClickListener {
            when (recordingState) {
                RecordingState.BEFORE_RECORDING -> {
                    startRecording()
                }
                RecordingState.ON_RECORDING -> {
                    pauseRecording()
                }
                RecordingState.PAUSE -> {
                    resumeRecording()
                }
            }
        }
        binding.stopButton.setOnClickListener {
            pauseRecording()
            with (AlertDialog.Builder(this)) {
                setTitle("녹음 파일을 저장하시겠습니까?")
                setPositiveButton("저장") { dialog, id ->
                    stopRecording()
                }
                setNegativeButton("취소") { dialog, id ->
                    // Do nothing
                }
                create()
                show()
            }
        }
        binding.cancelButton.setOnClickListener {
            pauseRecording()
            with (AlertDialog.Builder(this)) {
                setTitle("녹음을 취소하시겠습니까?")
                setPositiveButton("네") { dialog, id ->
                    cancelRecording()
                }
                setNegativeButton("아니오") { dialog, id ->
                    // Do nothing.
                }
                create()
                show()
            }
        }
    }

    // Checks if a volume containing external storage is available for read and write.
    private fun isExternalStorageWritable() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_list -> {
            startActivity(Intent(this, AudioListActivity::class.java))
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}