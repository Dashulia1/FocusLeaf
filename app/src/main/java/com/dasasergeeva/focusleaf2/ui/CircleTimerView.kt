package com.dasasergeeva.focusleaf2.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.dasasergeeva.focusleaf2.R
import kotlin.math.min

/**
 * Кастомный View для кругового таймера с прогрессом
 */
class CircleTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = ContextCompat.getColor(context, R.color.main_progress_not_done)
    }

    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.main_timer_color)
    }

    private val rectF = RectF()
    
    var progress: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val strokeWidth = paintBackground.strokeWidth
        val radius = (size - strokeWidth) / 2f
        
        rectF.set(
            strokeWidth / 2f,
            strokeWidth / 2f,
            size - strokeWidth / 2f,
            size - strokeWidth / 2f
        )

        // Рисуем фон (полный круг)
        canvas.drawArc(rectF, -90f, 360f, false, paintBackground)

        // Рисуем прогресс
        val sweepAngle = 360f * (progress / 100f)
        canvas.drawArc(rectF, -90f, sweepAngle, false, paintProgress)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(size, size)
    }
}


