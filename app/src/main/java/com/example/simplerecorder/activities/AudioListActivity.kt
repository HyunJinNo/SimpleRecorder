package com.example.simplerecorder.activities

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecorder.R
import com.example.simplerecorder.databinding.ActivityAudioListBinding
import com.example.simplerecorder.utils.AudioAdapter
import com.example.simplerecorder.utils.AudioData
import com.example.simplerecorder.utils.AudioPlayer
import java.io.File

class AudioListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioListBinding
    private val dataList = mutableListOf<AudioData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AudioAdapter(dataList)

        if (isExternalStorageReadable()) {
            val dir = Environment.getExternalStorageDirectory().absolutePath + "/Simple Recorder"
            if (!File(dir).exists()) {
                File(dir).mkdirs()
            }

            File(dir).listFiles()?.forEach {
                dataList.add(getAudioData(it.absolutePath))
                binding.recyclerView.adapter?.notifyItemInserted(dataList.lastIndex)
            }

            AudioPlayer.listener = MediaPlayer.OnPreparedListener {
                binding.customMediaController.playButton.setImageResource(R.drawable.baseline_play_arrow_24)

                val mmr = MediaMetadataRetriever().apply {
                    setDataSource(applicationContext, Uri.parse(AudioPlayer.filepath))
                }
                val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()!! / 1000

                binding.customMediaController.timeStamp1.text = "00:00:00"
                binding.customMediaController.timeStamp2.text = getTimeStamp(duration)

                binding.customMediaController.seekBar.max = duration
                binding.customMediaController.seekBar.progress = 0
            }
            AudioPlayer.ready(File(dir).listFiles()?.get(0)!!.absolutePath)
        }

        binding.customMediaController.backwardButton.setOnClickListener {
            val progress = binding.customMediaController.seekBar.progress
            binding.customMediaController.seekBar.progress = if (progress <= 5) 0 else (progress - 5)
        }

        binding.customMediaController.playButton.setOnClickListener {
            binding.customMediaController.playButton.setImageResource(R.drawable.baseline_pause_40)
            AudioPlayer.start()
        }

        binding.customMediaController.forwardButton.setOnClickListener {
            val progress = binding.customMediaController.seekBar.progress
            val max = binding.customMediaController.seekBar.max
            binding.customMediaController.seekBar.progress = if (progress + 5 >= max) max else (progress + 5)
        }

        binding.customMediaController.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.customMediaController.timeStamp1.text = getTimeStamp(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                binding.customMediaController.timeStamp1.text = getTimeStamp(seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.customMediaController.timeStamp1.text = getTimeStamp(seekBar.progress)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        AudioPlayer.mediaPlayer?.run {
            stop()
            reset()
            release()
        }
        AudioPlayer.mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioPlayer.mediaPlayer = null
    }

    // Checks if a volume containing external storage is available to at least read.
    private fun isExternalStorageReadable() = Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)

    private fun getAudioData(path: String): AudioData {
        val filename = path.split("/").last().split(".").first()
        val mmr = MediaMetadataRetriever().apply {
            setDataSource(applicationContext, Uri.parse(path))
        }
        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()!! / 1000
        val durationStr = getTimeStamp(duration)
        val date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)!!
        val dateStr = buildString {
            append(date.slice(0..3))
            append("-")
            append(date.slice(4..5))
            append("-")
            append(date.slice(6..7))
        }

        return AudioData(path, filename, durationStr, dateStr)
    }

    private fun getTimeStamp(duration: Int) = buildString {
        if (duration / 3600 < 10) append("0")
        append(duration / 3600)
        append(":")
        if ((duration % 3600) / 60 < 10) append("0")
        append((duration % 3600) / 60)
        append(":")
        if (duration % 60 < 10) append("0")
        append(duration % 60)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}