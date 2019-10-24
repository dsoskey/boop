package com.example.boop.pad

import android.graphics.Color
import com.example.boop.R
import com.example.boop.color.gradient

class ColorScheme {

    private val colorToId: MutableMap<Color, Set<Int>>

    val colors: Set<Color>
        get() = colorToId.keys

    fun buttonToColor(): MutableMap<Int, Int> {
        val btc = HashMap<Int, Int>()
        for (color in colors) {
            for (id in getIds(color)) {
                btc[id] = color.toArgb()
            }
        }
        return btc
    }

    init {
        colorToId = HashMap()
    }

    fun getIds(color: Color): Set<Int> {
        return colorToId[color]!!
    }

    fun withColorMap(color: Color, ids: Set<Int>): ColorScheme {
        colorToId[color] = ids
        return this
    }

    fun popColorMap(color: Color): Set<Int> {
        return colorToId.remove(color)!!
    }

    companion object {
        fun HORIZONTAL_STRIPED(color1: Color, color2: Color): ColorScheme {
            return ColorScheme()
                .withColorMap(color1, hashSetOf(
                    R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3,
                    R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11))
                .withColorMap(color2, hashSetOf(
                    R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7,
                    R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15))
        }

        fun CHECKERED(color1: Color, color2: Color): ColorScheme {
            return ColorScheme()
                .withColorMap(color1, hashSetOf(
                    R.id.grid_0, R.id.grid_2, R.id.grid_5, R.id.grid_7,
                    R.id.grid_8, R.id.grid_10, R.id.grid_13, R.id.grid_15))
                .withColorMap(color2, hashSetOf(
                    R.id.grid_1, R.id.grid_3, R.id.grid_4, R.id.grid_6,
                    R.id.grid_9, R.id.grid_11, R.id.grid_12, R.id.grid_14))
        }

        fun DIAGONAL(color1: Color, color2: Color): ColorScheme {
            val gradient = gradient(color1, color2, 7)
            val scheme = ColorScheme()
            return scheme
                .withColorMap(gradient[0], hashSetOf(R.id.grid_0))
                .withColorMap(gradient[1], hashSetOf(R.id.grid_1, R.id.grid_4))
                .withColorMap(gradient[2], hashSetOf(R.id.grid_2, R.id.grid_5, R.id.grid_8))
                .withColorMap(gradient[3], hashSetOf(R.id.grid_3, R.id.grid_6, R.id.grid_9, R.id.grid_12))
                .withColorMap(gradient[4], hashSetOf(R.id.grid_7, R.id.grid_10, R.id.grid_13))
                .withColorMap(gradient[5], hashSetOf(R.id.grid_11, R.id.grid_14))
                .withColorMap(gradient[6], hashSetOf(R.id.grid_15))
        }

        fun PIANO(whiteKey: Color, blackKey: Color): ColorScheme {
            return ColorScheme()
                .withColorMap(whiteKey, hashSetOf(
                    R.id.grid_0, R.id.grid_2, R.id.grid_4, R.id.grid_5, R.id.grid_7,
                    R.id.grid_9, R.id.grid_11, R.id.grid_12, R.id.grid_14))
                .withColorMap(blackKey, hashSetOf(
                    R.id.grid_1, R.id.grid_3, R.id.grid_6,
                    R.id.grid_8, R.id.grid_10, R.id.grid_13, R.id.grid_15))
        }
    }
}
