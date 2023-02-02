package wav.boop.color

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import wav.boop.R

/*
 * Calculates the colors needed for a gradient between two colors within step steps including them.
 * Note: returned color list will have length step
 */
fun gradient(color1: Color, color2: Color, step: Int): List<Color> {
    val redStep = (color2.red() - color1.red()) / step
    val greenStep = (color2.green() - color1.green()) / step
    val blueStep = (color2.blue() - color1.blue()) / step

    val gradient = mutableListOf<Color>()
    for (i in 0 until step) {
        val nextRed = color1.red() + redStep * i
        val nextGreen = color1.green() + greenStep * i
        val nextBlue = color1.blue() + blueStep * i

        val nextColor = Color.valueOf(nextRed, nextGreen, nextBlue)

        gradient.add(nextColor)
    }

    gradient.add(color2)

    return gradient
}

fun getThemeColor(theme: Resources.Theme, resourceId: Int): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(resourceId, tv, true)) {
        tv.data
    } else {
        R.color.meat
    }
}
