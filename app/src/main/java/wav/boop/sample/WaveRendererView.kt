package wav.boop.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveRendererView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    // We want to ensure that the lines drawn don't bleed off the canvas, so we shrink the normalized data to fit
    var data: FloatArray = floatArrayOf()
        set(value) {
            // TODO: Move silence chopping to a place that lets all of the app get the chopped sample
            val firstSilence = value.indexOfLast { it != 0f } + 1
            val withoutSilence = value.sliceArray(0 until firstSilence)
            val xStep: Float = width.toFloat() / withoutSilence.size
            val yMid: Float = height / 2f
            // X is evenly distributed among [xMin, xMax]
            normedX = withoutSilence.mapIndexed { frame, _ -> frame * xStep }.toFloatArray()
            // Y is already normalized [-1, 1], so it can be transformed to represent distance away from the midpoint of the view
            normedY = withoutSilence.map { waveVal -> yMid + yMid * waveVal }.toFloatArray()
            field = withoutSilence
            invalidate()
        }
    var normedX: FloatArray = floatArrayOf()
    var normedY: FloatArray = floatArrayOf()
    val paintOdd = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 2f
    }
    val paintEven = Paint().apply {
        color = Color.GREEN
        strokeWidth = 2f
    }
    override fun onDraw(canvas: Canvas) {
        data.forEachIndexed { index, _ ->
            if (index > 0) {
                val paint = if (index % 2 == 0) paintEven else paintOdd
                canvas.drawLine(normedX[index - 1], normedY[index - 1], normedX[index], normedY[index], paint)
            }
        }
        super.onDraw(canvas)
    }
}