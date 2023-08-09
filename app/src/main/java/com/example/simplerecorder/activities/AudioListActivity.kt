package com.example.simplerecorder.activities

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
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
    private val handler = Handler(Looper.getMainLooper())
    private val seekBarAction: Runnable = object : Runnable {
        override fun run() {
            if (AudioPlayer.isPlaying) {
                binding.customMediaController.seekBar.progress = AudioPlayer.currentPosition
                handler.postDelayed(this, 1L)
            } else {
                binding.customMediaController.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                handler.removeCallbacks(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AudioAdapter(dataList)

        initListeners()

        if (isExternalStorageReadable()) {
            val dir = Environment.getExternalStorageDirectory().absolutePath + "/Simple Recorder"
            if (!File(dir).exists()) {
                File(dir).mkdirs()
            }

            File(dir).listFiles()?.forEach {
                dataList.add(getAudioData(it.absolutePath))
                binding.recyclerView.adapter?.notifyItemInserted(dataList.lastIndex)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.customMediaController.root.visibility = View.INVISIBLE
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
        AudioPlayer.mediaPlayer?.run {
            stop()
            reset()
            release()
        }
        AudioPlayer.mediaPlayer = null
    }

    private fun initListeners() {
        AudioPlayer.listener = MediaPlayer.OnPreparedListener {
            val mmr = MediaMetadataRetriever().apply {
                setDataSource(applicationContext, Uri.parse(AudioPlayer.filepath))
            }
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()!!

            binding.customMediaController.root.visibility = View.VISIBLE
            binding.customMediaController.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            binding.customMediaController.timeStamp1.text = "00:00:00"
            binding.customMediaController.timeStamp2.text = getTimeStamp(duration / 1000)
            binding.customMediaController.seekBar.max = duration
            binding.customMediaController.seekBar.progress = 0
        }

        binding.customMediaController.backwardButton.setOnClickListener {
            val progress = binding.customMediaController.seekBar.progress
            binding.customMediaController.seekBar.progress = progress - 5000
            AudioPlayer.seekTo(binding.customMediaController.seekBar.progress)
        }

        binding.customMediaController.playButton.setOnClickListener {
            if (!AudioPlayer.isPlaying) {
                binding.customMediaController.playButton.setImageResource(R.drawable.baseline_pause_40)
                AudioPlayer.start()
                handler.post(seekBarAction)
            } else {
                binding.customMediaController.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                AudioPlayer.pause()
                handler.removeCallbacks(seekBarAction)
            }
        }

        binding.customMediaController.forwardButton.setOnClickListener {
            val progress = binding.customMediaController.seekBar.progress
            binding.customMediaController.seekBar.progress = progress + 5000
            AudioPlayer.seekTo(binding.customMediaController.seekBar.progress)
        }

        binding.customMediaController.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.customMediaController.timeStamp1.text = getTimeStamp(progress / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                binding.customMediaController.timeStamp1.text = getTimeStamp(seekBar.progress / 1000)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.customMediaController.timeStamp1.text = getTimeStamp(seekBar.progress / 1000)
                AudioPlayer.seekTo(seekBar.progress)
            }
        })
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

    private fun getTimeStamp(countTimeSeconds: Int): String {
        val hour = countTimeSeconds / 3600
        val minute = (countTimeSeconds % 3600) / 60
        val second = countTimeSeconds % 60
        return "%02d:%02d:%02d".format(hour, minute, second)
    }

    /*
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
    */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AudioPlayer.mediaPlayer?.run {
            stop()
            reset()
            release()
        }
        AudioPlayer.mediaPlayer = null
        finish()
    }
}