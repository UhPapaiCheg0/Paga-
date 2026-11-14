package com.pagae.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.pagae.app.R
import com.pagae.app.db.DatabaseHelper
import kotlin.math.max

/** Gráfico simples, nativo e sem dependência externa para manter o app leve/offline. */
class MonthlySalesChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private var values: List<DatabaseHelper.MonthlySales> = emptyList()
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.gold)
        style = Paint.Style.FILL
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.gray_hint)
        textSize = sp(10f)
        textAlign = Paint.Align.CENTER
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.gold)
        textSize = sp(9f)
        textAlign = Paint.Align.CENTER
    }
    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.gray_hint)
        textSize = sp(12f)
        textAlign = Paint.Align.CENTER
    }

    fun submitData(data: List<DatabaseHelper.MonthlySales>) {
        values = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val data = values
        if (data.isEmpty()) {
            canvas.drawText("As vendas aparecerão aqui", width / 2f, height / 2f, emptyPaint)
            return
        }
        val left = paddingLeft + dp(8f)
        val right = width - paddingRight - dp(8f)
        val top = paddingTop + dp(20f)
        val bottom = height - paddingBottom - dp(26f)
        val chartHeight = max(1f, bottom - top)
        val maxValue = max(1.0, data.maxOf { it.total })
        val slot = (right - left) / data.size
        val barWidth = minOf(dp(24f), slot * .48f)

        data.forEachIndexed { index, item ->
            val centerX = left + slot * (index + .5f)
            val barHeight = (item.total / maxValue * chartHeight).toFloat()
            val barTop = bottom - barHeight
            canvas.drawRoundRect(centerX - barWidth / 2f, barTop, centerX + barWidth / 2f,
                bottom, dp(5f), dp(5f), barPaint)
            val amountLabel = if (item.total >= 1000) "${(item.total / 1000).toInt()}k" else item.total.toInt().toString()
            canvas.drawText(amountLabel, centerX, max(top - dp(4f), barTop - dp(5f)), valuePaint)
            canvas.drawText(item.month, centerX, height - paddingBottom - dp(7f), labelPaint)
        }
    }

    private fun dp(value: Float) = value * resources.displayMetrics.density
    private fun sp(value: Float) = value * resources.displayMetrics.scaledDensity
}
