package ntdalbec.sequencinator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class SequencerView(context: Context, attrs: AttributeSet) : View(context, attrs), Observer {
    private val barColor = Color.BLUE
    private val paint = Paint()
    private val notes = mutableMapOf<Observable, List<Note>>()
    private var rectLists: List<List<Rect>> = listOf()
    private var neededWidth: Int
    private val screenSize = Point()

    var startTone = 40
    var endTone = 51

    private fun buildRects() {
        val widthMult = 16
        val barHeight = height / (endTone + 1 - startTone)
        var longest = 0

        rectLists = notes.values.map(fun(it: List<Note>): List<Rect> {
            var xPos = 0
            val rects = it.map(fun(it: Note): Rect {
                val end = xPos + it.duration * widthMult
                val top = height - (it.tone - startTone) * barHeight
                val bottom = top - barHeight
                val rect = Rect(xPos, top, end, bottom)
                xPos = end
                return rect
            })
            longest = if (xPos > longest) xPos else longest

            return rects
        })

        neededWidth = longest
    }

    override fun update(o: Observable?, arg: Any?) {
        notes[o!!] = arg as List<Note> // Assumes that either being null is not a valid state
        buildRects()
        Log.i(LOG_TAG, "observer update")
        requestLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        Log.i(LOG_TAG, "onDraw: ${rectLists.size}")
        if (canvas == null) return
        rectLists.forEach {
            Log.i(LOG_TAG, "rect draw: ${it.size}")
            it.forEach{
                canvas.drawRect(it, paint)
            }
        }
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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