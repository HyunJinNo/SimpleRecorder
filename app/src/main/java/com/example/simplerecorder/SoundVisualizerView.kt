package com.example.simplerecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundVisualizerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.black)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isRePlaying: Boolean = false
    private var replayingPosition: Int = 0

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 35L
    }

    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if (!isRePlaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                replayingPosition++
            }
            invalidate()
            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    var onRequestCurrentAmplitude: (() -> Int)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2F
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes.run {
            if (isRePlaying) {
                this.takeLast(replayingPosition)
            }
            else {
                this
            }
        }.forEach {
            val lineLength = it / MAX_AMPLITUDE * drawingHeight * 0.8F
            offsetX -= LINE_SPACE

            if (offsetX < 0) {
                return@forEach
            }

            canvas.drawLine(offsetX, centerY - (lineLength / 2F), offsetX, centerY + (lineLength / 2F), amplitudePaint)
        }
    }

    fun startVisualizing(isReplaying: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(500L)
            isRePlaying = isReplaying
            handler?.post(visualizeRepeatAction)
        }
    }

    fun stopVisualizing() {
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun resumeVisualizing() {
        handler?.post(visualizeRepeatAction)
    }

    fun clearVisualizing() {
        drawingAmplitudes = emptyList()
        isRePlaying = false
        replayingPosition = 0
        stopVisualizing()
        invalidate()
    }
}