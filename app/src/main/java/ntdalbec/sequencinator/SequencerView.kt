package ntdalbec.sequencinator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class SequencerView(context: Context, attrs: AttributeSet) : View(context, attrs), Observer {
    private val barColor = Color.BLUE
    private val paint = Paint()
    private val notes = mutableMapOf<Observable, List<Note>>()
    private var neededWidth: Int
    private val screenSize = Point()

    var startTone = 40
    var endTone = 51

        override fun update(o: Observable?, arg: Any?) {
        notes[o!!] = arg as List<Note> // Assumes that either being null is not a valid state
        Log.i(LOG_TAG, "observer update")
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        Log.i(LOG_TAG, "onDraw")
        super.onDraw(canvas)
        if (canvas == null) return

        val widthMult = 16F //TODO: use something screen density aware
        val barHeight = height / (endTone + 1 - startTone).toFloat()
        var longest = 0

        notes.values.forEach {
            var xPos = 0F
            it.forEach {
                val end = xPos + it.duration * widthMult
                val top = (it.tone - startTone) * barHeight
                val bottom = top + barHeight
                canvas.drawRect(xPos, top, end, bottom, paint)
                xPos = end
            }
            longest = if (xPos > longest) xPos.toInt() else longest
        }

        neededWidth = longest
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO: cause to resize properly
        Log.i(LOG_TAG, "onMeasure")
        val heightSpec = MeasureSpec.getSize(heightMeasureSpec)

        display.getSize(screenSize)
        val currentWidth = Math.max(neededWidth, screenSize.x)

        setMeasuredDimension(currentWidth, heightSpec)
    }

    init {
        isFocusableInTouchMode = true

        paint.color = barColor
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        neededWidth = 32
    }
}