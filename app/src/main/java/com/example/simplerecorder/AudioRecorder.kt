package com.example.simplerecorder

import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

object AudioRecorder {
    var recorder: MediaRecorder? = null
    var filepath: String = ""
    var recordingState = RecordingState.BEFORE_RECORDING

    fun startRecording() {
        if (!isExternalStorageWritable()) {
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

        recordingState = RecordingState.ON_RECORDING
    }

    fun resumeRecording() {
        recorder?.resume()
        recordingState = RecordingState.ON_RECORDING
    }

    fun pauseRecording() {
        recorder?.pause()
        recordingState = RecordingState.PAUSE
    }

    fun stopRecording() {
        recorder?.run {
            stop()
            reset()
            release()
        }

        recordingState = RecordingState.BEFORE_RECORDING
        recorder = null
    }

    fun cancelRecording() {
        stopRecording()
        File(filepath).delete()
    }

    // Checks if a volume containing external storage is available for read and write.
    private fun isExternalStorageWritable() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}