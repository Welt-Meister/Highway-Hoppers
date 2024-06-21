package com.example.highwayhoppers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class VerticalLinesView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = android.graphics.Color.WHITE
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate the width and height of the view
        val viewWidth = width
        val viewHeight = height

        // Calculate the positions for the three vertical lines
        val line1X = viewWidth / 3f
        val line2X = (2 * viewWidth) / 3f


        // Draw the three vertical lines
        canvas.drawLine(line1X, 0f, line1X, viewHeight.toFloat(), paint)
        canvas.drawLine(line2X, 0f, line2X, viewHeight.toFloat(), paint)




    }
}