package wav.boop.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import wav.boop.R

class WaveRendererView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    private val padIdsToRenderedSample: MutableMap<Int, List<Pair<Float, Float>>> = HashMap()
    private var currentPadId: Int = R.id.pad_0

    fun hasData(padId: Int): Boolean { return padIdsToRenderedSample[padId] != null }
    fun loadData(padId: Int, sampleData: FloatArray) {
        padIdsToRenderedSample[padId] = renderedSample(sampleData, width, height)
    }
    fun renderData(padId: Int) {
        if (padIdsToRenderedSample.containsKey(padId)) {
            currentPadId = padId
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val sample = padIdsToRenderedSample[currentPadId]
        if (sample != null) {
            val paints = padIdsToPaint[currentPadId]!!


            sample.forEachIndexed { index, _ ->
                if (index > 0) {
                    val paint = if (index % 2 == 0) paints.first else paints.second
                    canvas.drawLine(sample[index - 1].first, sample[index - 1].second, sample[index].first, sample[index].second, paint)
                }
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