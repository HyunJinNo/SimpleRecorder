package com.example.simplerecorder

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerecorder.databinding.ActivityAudioListBinding
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
        }
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
        val date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)!!
        val durationStr = buildString {
            if (duration / 3600 < 10) append("0")
            append(duration / 3600)
            append(":")
            if ((duration % 3600) / 60 < 10) append("0")
            append((duration % 3600) / 60)
            append(":")
            if (duration % 60 < 10) append("0")
            append(duration % 60)
        }

        val dateStr = buildString {
            append(date.slice(0..3))
            append("-")
            append(date.slice(4..5))
            append("-")
            append(date.slice(6..7))
        }

        return AudioData(path, filename, durationStr, dateStr)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}