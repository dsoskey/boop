package wav.boop.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import wav.boop.R

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
    private var paintOdd = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 2f
    }
    private var paintEven = Paint().apply {
        color = Color.GREEN
        strokeWidth = 2f
    }
    fun setData(padId: Int, sampleData: FloatArray) {
        val paint = padIdsToPaint[padId]
        if (paint != null) {
            paintOdd = paint.first
            paintEven = paint.second
        }
        data = sampleData
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

    companion object {
        val padIdsToPaint: Map<Int, Pair<Paint, Paint>> = mapOf(
            R.id.pad_0 to Pair(
                Paint().apply {
                    color = Color.parseColor("#9C27B0")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#BA68C8");
                }),
            R.id.pad_1 to Pair(
                Paint().apply {
                    color = Color.parseColor("#3F51B5")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#7986CB")
                    strokeWidth = 2f
                }),
            R.id.pad_2 to Pair(
                Paint().apply {
                    color = Color.parseColor("#2196F3")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#64B5F6")
                    strokeWidth = 2f
                }),
            R.id.pad_3 to Pair(
                Paint().apply {
                    color = Color.parseColor("#03A9F4")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#4FC3F7")
                    strokeWidth = 2f
                }),
            R.id.pad_4 to Pair(
                Paint().apply {
                    color = Color.parseColor("#00BCD4")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#4DD0E1")
                    strokeWidth = 2f
                }),
            R.id.pad_5 to Pair(
                Paint().apply {
                    color = Color.parseColor("#009688")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#4DB6AC")
                    strokeWidth = 2f
                }),
            R.id.pad_6 to Pair(
                Paint().apply {
                    color = Color.parseColor("#4CAF50")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#81C784")
                    strokeWidth = 2f
                }),
            R.id.pad_7 to Pair(
                Paint().apply {
                    color = Color.parseColor("#8BC34A")
                    strokeWidth = 2f
                },
                Paint().apply {
                    color = Color.parseColor("#AED581")
                    strokeWidth = 2f
                })
        )
    }
}