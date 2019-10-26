package com.example.boop.pad

import android.graphics.Color
import com.example.boop.R
import com.example.boop.color.gradient

class ColorScheme(var defaultValue: Color = Color.valueOf(Color.BLACK)) {

    val idList: List<Int> = listOf(
        R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15,
        R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11,
        R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7,
        R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3
    )
    private val idToColor: MutableMap<Int, Color> = HashMap()

    val colors: Set<Color>
        get() = idToColor.values.toSet()


    fun getColor(id: Int): Color {
        return idToColor.getOrDefault(id, defaultValue)
    }

    fun withColorList(colorList: List<Color>): ColorScheme {
        for((index, color) in colorList.listIterator().withIndex()) {
            if (index < idList.size) {
                idToColor[idList[index]] = color
            }
        }
        return this
    }

    fun withColor(id: Int, color: Color): ColorScheme {
        idToColor[id] = color
        return this
    }

    companion object {
        fun horizontalStriped(color1: Color, color2: Color): ColorScheme {
            return ColorScheme()
                .withColorList(
                    listOf(
                        color1, color1, color1, color1,
                        color2, color2, color2, color2,
                        color1, color1, color1, color1,
                        color2, color2, color2, color2
                    )
                )
        }

        fun checkered(color1: Color, color2: Color): ColorScheme {
            return ColorScheme()
                .withColorList(
                    listOf(
                        color1, color2, color1, color2,
                        color2, color1, color2, color1,
                        color1, color2, color1, color2,
                        color2, color1, color2, color1
                    )
                )
        }

        fun diagonal(color1: Color, color2: Color): ColorScheme {
            val gradient = gradient(color1, color2, 7)
            return ColorScheme()
                .withColorList(
                    listOf(
                        gradient[3], gradient[4], gradient[5], gradient[6],
                        gradient[2], gradient[3], gradient[4], gradient[5],
                        gradient[1], gradient[2], gradient[3], gradient[4],
                        gradient[0], gradient[1], gradient[2], gradient[3]
                    )
                )
        }

        fun piano(whiteKey: Color, blackKey: Color): ColorScheme {
            return ColorScheme()
                .withColorList(
                    listOf(
                        whiteKey, blackKey, whiteKey, blackKey,
                        blackKey, whiteKey, blackKey, whiteKey,
                        whiteKey, whiteKey, blackKey, whiteKey,
                        whiteKey, blackKey, whiteKey, blackKey
                    )
                )
        }

        fun monochrome(color: Color): ColorScheme {
            return ColorScheme().withColorList(listOf(
                color, color, color, color,
                color, color, color, color,
                color, color, color, color,
                color, color, color, color
            ))
        }
    }
}
