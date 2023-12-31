package com.example.simplerecorder.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerecorder.R

class AudioAdapter(private val dataList: MutableList<AudioData>)
    : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    // Create new views (invoked by the layout manager)
    // 아이템 뷰를 위한 ViewHolder 객체를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    // position 에 해당하는 데이터를 ViewHolder 의 아이템 뷰에 표시
    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        // Get element from your dataset at this position and
        // replace the contents of the view with that element
        holder.filepath = dataList[position].filepath
        holder.filename.text = dataList[position].filename
        holder.duration.text = dataList[position].duration
        holder.date.text = dataList[position].date

        holder.cardView.setOnClickListener {
            AudioPlayer.ready(holder.filepath)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    // 전체 데이터 수를 반환
    override fun getItemCount(): Int = dataList.size

    // 아이템 뷰를 저장하는 ViewHolder 클래스
    class AudioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var filepath: String
        val cardView: CardView
        val imageView: ImageView
        val filename: TextView
        val duration: TextView
        val date: TextView

        init {
            // 뷰 객체에 대한 참조
            cardView = view.findViewById(R.id.cardView)
            imageView = view.findViewById(R.id.imageView)
            filename = view.findViewById(R.id.filenameTextView)
            duration = view.findViewById(R.id.durationTextView)
            date = view.findViewById(R.id.dateTextView)
        }
    }
}