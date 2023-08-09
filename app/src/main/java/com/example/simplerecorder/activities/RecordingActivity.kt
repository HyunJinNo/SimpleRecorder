package com.example.simplerecorder.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.PreferenceManager
import com.example.simplerecorder.utils.AudioRecorder
import com.example.simplerecorder.R
import com.example.simplerecorder.utils.RecordingState
import com.example.simplerecorder.databinding.ActivityRecordingBinding
import java.io.File

class RecordingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordingBinding

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
            AudioRecorder.recorder?.maxAmplitude ?: 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (AudioRecorder.recorder != null) {
            cancelRecording()
        }
        AudioRecorder.recorder = null
    }

    private fun startRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext,
            R.drawable.baseline_pause_40
        ))
        binding.stopButton.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.VISIBLE
        binding.countUpView.startCountUp()
        binding.soundVisualizerView.startVisualizing(isReplaying = false)
        AudioRecorder.startRecording()
    }

    private fun resumeRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext,
            R.drawable.baseline_pause_40
        ))
        binding.countUpView.resumeCountUp()
        binding.soundVisualizerView.resumeVisualizing()
        AudioRecorder.resumeRecording()
    }

    private fun pauseRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext,
            R.drawable.baseline_fiber_manual_record_40
        ))
        binding.countUpView.stopCountUp()
        binding.soundVisualizerView.stopVisualizing()
        AudioRecorder.pauseRecording()
    }

    private fun stopRecording() {
        binding.recordButton.setImageDrawable(AppCompatResources.getDrawable(applicationContext,
            R.drawable.baseline_fiber_manual_record_40
        ))
        binding.stopButton.visibility = View.INVISIBLE
        binding.cancelButton.visibility = View.INVISIBLE
        binding.countUpView.clearCountTime()
        binding.soundVisualizerView.clearVisualizing()
        Toast.makeText(applicationContext, "녹음 종료", Toast.LENGTH_SHORT).show()
        AudioRecorder.stopRecording()
    }

    private fun cancelRecording() {
        stopRecording()
        File(AudioRecorder.filepath).delete()
        Toast.makeText(applicationContext, "녹음 취소", Toast.LENGTH_SHORT).show()
    }

    private fun initListeners() {
        binding.recordButton.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            if (prefs.getBoolean("foregroundSetting", false)) {
                Toast.makeText(applicationContext, "포어그라운드 서비스가 동작 중입니다.", Toast.LENGTH_SHORT).show()
            } else {
                when (AudioRecorder.recordingState) {
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
        }
        binding.stopButton.setOnClickListener {
            pauseRecording()
            with (AlertDialog.Builder(this)) {
                setTitle("녹음 파일을 저장하시겠습니까?")
                setPositiveButton("저장") { _, _ ->
                    stopRecording()
                }
                setNegativeButton("취소") { _, _ ->
                    // Do nothing.
                }
                create()
                show()
            }
        }
        binding.cancelButton.setOnClickListener {
            pauseRecording()
            with (AlertDialog.Builder(this)) {
                setTitle("녹음을 취소하시겠습니까?")
                setPositiveButton("네") { _, _ ->
                    cancelRecording()
                }
                setNegativeButton("아니오") { _, _ ->
                    // Do nothing.
                }
                create()
                show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_list -> {
            if (AudioRecorder.recorder != null) {
                when (AudioRecorder.recordingState) {
                    RecordingState.ON_RECORDING -> {
                        pauseRecording()
                        with (AlertDialog.Builder(this)) {
                            setTitle("녹음 파일을 저장하시겠습니까?")
                            setPositiveButton("저장") { _, _ ->
                                stopRecording()
                                startActivity(Intent(this@RecordingActivity, AudioListActivity::class.java))
                            }
                            setNegativeButton("저장 안함") { _, _ ->
                                cancelRecording()
                                startActivity(Intent(this@RecordingActivity, AudioListActivity::class.java))
                            }
                            setNeutralButton("취소") { _, _ ->
                                // Do nothing.
                            }
                            create()
                            show()
                        }
                    }
                    RecordingState.PAUSE -> {
                        with (AlertDialog.Builder(this)) {
                            setTitle("녹음 파일을 저장하시겠습니까?")
                            setPositiveButton("저장") { _, _ ->
                                stopRecording()
                                startActivity(Intent(this@RecordingActivity, AudioListActivity::class.java))
                            }
                            setNegativeButton("저장 안함") { _, _ ->
                                cancelRecording()
                                startActivity(Intent(this@RecordingActivity, AudioListActivity::class.java))
                            }
                            setNeutralButton("취소") { _, _ ->
                                // Do nothing.
                            }
                            create()
                            show()
                        }
                    }
                    else -> {
                        startActivity(Intent(this, AudioListActivity::class.java))
                    }
                }
            } else {
                startActivity(Intent(this, AudioListActivity::class.java))
            }

            true
        }
        R.id.action_settings -> {
            if (AudioRecorder.recorder != null) {
                when (AudioRecorder.recordingState) {
                    RecordingState.ON_RECORDING -> {
                        pauseRecording()
                        with (AlertDialog.Builder(this)) {
                            setTitle("녹음 파일을 저장하시겠습니까?")
                            setPositiveButton("저장") { _, _ ->
                                stopRecording()
                                startActivity(Intent(this@RecordingActivity, SettingsActivity::class.java))
                            }
                            setNegativeButton("저장 안함") { _, _ ->
                                cancelRecording()
                                startActivity(Intent(this@RecordingActivity, SettingsActivity::class.java))
                            }
                            setNeutralButton("취소") { _, _ ->
                                // Do nothing.
                            }
                            create()
                            show()
                        }
                    }
                    RecordingState.PAUSE -> {
                        with (AlertDialog.Builder(this)) {
                            setTitle("녹음 파일을 저장하시겠습니까?")
                            setPositiveButton("저장") { _, _ ->
                                stopRecording()
                                startActivity(Intent(this@RecordingActivity, SettingsActivity::class.java))
                            }
                            setNegativeButton("저장 안함") { _, _ ->
                                cancelRecording()
                                startActivity(Intent(this@RecordingActivity, SettingsActivity::class.java))
                            }
                            setNeutralButton("취소") { _, _ ->
                                // Do nothing.
                            }
                            create()
                            show()
                        }
                    }
                    else -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                }
            } else {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (AudioRecorder.recorder != null) {
            when (AudioRecorder.recordingState) {
                RecordingState.ON_RECORDING -> {
                    pauseRecording()
                    with (AlertDialog.Builder(this)) {
                        setTitle("녹음 파일을 저장하시겠습니까?")
                        setPositiveButton("저장") { _, _ ->
                            stopRecording()
                            finish()
                        }
                        setNegativeButton("저장 안함") { _, _ ->
                            cancelRecording()
                            finish()
                        }
                        setNeutralButton("취소") { _, _ ->
                            // Do nothing.
                        }
                        create()
                        show()
                    }
                }
                RecordingState.PAUSE -> {
                    with (AlertDialog.Builder(this)) {
                        setTitle("녹음 파일을 저장하시겠습니까?")
                        setPositiveButton("저장") { _, _ ->
                            stopRecording()
                            finish()
                        }
                        setNegativeButton("저장 안함") { _, _ ->
                            cancelRecording()
                            finish()
                        }
                        setNeutralButton("취소") { _, _ ->
                            // Do nothing.
                        }
                        create()
                        show()
                    }
                }
                else -> {
                    finish()
                }
            }
        } else {
            finish()
        }
    }
}