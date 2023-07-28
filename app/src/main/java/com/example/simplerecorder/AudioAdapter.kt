package com.example.simplerecorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioAdapter(private val dataSet: Array<String>) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {
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
        holder.textView.text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    // 전체 데이터 수를 반환
    override fun getItemCount(): Int = dataSet.size

    // 아이템 뷰를 저장하는 ViewHolder 클래스
    class AudioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // 뷰 객체에 대한 참조
            textView = view.findViewById(R.id.audioTextView)
        }
    }
}