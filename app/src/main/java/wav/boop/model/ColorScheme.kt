package wav.boop.model

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wav.boop.R
import wav.boop.color.gradient
import wav.boop.pad.padIds
import wav.boop.pitch.Scale

data class ColorAssignment(var color: Color, val padIds: MutableSet<Int>)

/**
 * ViewModel that handles the color scheme for the pads.
 * In the future this might hold the color scheme for the entire app.
 */
class ColorScheme: ViewModel() {
    enum class Preset(val spinnerText: String) {
        IONIAN("major (C)"),
        DORIAN("dorian (D)"),
        PHRYGIAN("phrygian (E)"),
        LYDIAN("lydian (F)"),
        MIXOLYDIAN("mixolydian (G)"),
        AEOLIAN("minor (A)"),
        LOCRIAN("locrian (B)"),
        DIAG_GRADIENT("diagonal gradient"),
        CHECKER("checkerboard"),
        VERTICAL_STRIPE("vertical stripe"),
        HORIZONTAL_STRIPE("horizontal stripe"),
        MONOCHROME("monochrome")
    }
    val colorAssignment = MutableLiveData<List<ColorAssignment>>(listOf())

    fun getColor(padId: Int): Color? = colorAssignment.value!!.find { ca -> ca.padIds.contains(padId) }?.color
    fun getAssignment(index: Int): ColorAssignment? = colorAssignment.value!!.getOrNull(index)
    fun getColorAtIndex(index: Int): Color? = colorAssignment.value!!.getOrNull(index)?.color
    fun setColorForButtons(color: Color, buttonIds: List<Int>) { setColorForButtons(color, buttonIds.toSet()) }
    fun setColorForButtons(color: Color, vararg buttonIds: Int) { setColorForButtons(color, buttonIds.toSet()) }
    fun setColorForButtons(color: Color, buttonIds: Set<Int>) {
        val buttonsClone = buttonIds.toMutableSet()
        val newConfig = mutableListOf<ColorAssignment>()
        var foundColor = false
        colorAssignment.value!!.forEach { ca ->
            if (color == ca.color) {
                ca.padIds.addAll(buttonIds)
                foundColor = true
            } else {
                ca.padIds.removeAll(buttonIds)
            }
            newConfig.add(ca)
        }
        newConfig.removeIf { ca -> ca.padIds.size == 0 }
        if (!foundColor) {
            newConfig.add(ColorAssignment(color, buttonsClone))
        }
        colorAssignment.value = newConfig.toList()
    }

    // Preset functions
    fun makeMode(whiteKey: Color, blackKey: Color, scale: Scale) {
        setColorForButtons(whiteKey, padIds.filterIndexed { index, _ -> scale.steps.contains(index % 12) })
        setColorForButtons(blackKey, padIds.filterIndexed { index, _ -> !scale.steps.contains(index.rem(12)) })
    }
    fun makeDiagonalGrandient(bottomColor: Color, topColor:Color) {
        val gradient = gradient(bottomColor, topColor, 7)
        setColorForButtons(gradient[0], R.id.grid_0)
        setColorForButtons(gradient[1], R.id.grid_1, R.id.grid_4)
        setColorForButtons(gradient[2], R.id.grid_2, R.id.grid_5, R.id.grid_8)
        setColorForButtons(gradient[3], R.id.grid_3, R.id.grid_6, R.id.grid_9, R.id.grid_12)
        setColorForButtons(gradient[4], R.id.grid_7, R.id.grid_10, R.id.grid_13)
        setColorForButtons(gradient[5], R.id.grid_11, R.id.grid_14)
        setColorForButtons(gradient[6], R.id.grid_15)
    }
    fun makeChecker(color1: Color, color2: Color) {
        setColorForButtons(color1, R.id.grid_0, R.id.grid_2, R.id.grid_5, R.id.grid_7, R.id.grid_8, R.id.grid_10, R.id.grid_13, R.id.grid_15)
        setColorForButtons(color2, R.id.grid_1, R.id.grid_3, R.id.grid_4, R.id.grid_6, R.id.grid_9, R.id.grid_11, R.id.grid_12, R.id.grid_14)
    }
    fun makeVerticalStripe(color1: Color, color2: Color) {
        setColorForButtons(color1, R.id.grid_0, R.id.grid_2, R.id.grid_4, R.id.grid_6, R.id.grid_8, R.id.grid_10, R.id.grid_12, R.id.grid_14)
        setColorForButtons(color2, R.id.grid_1, R.id.grid_3, R.id.grid_5, R.id.grid_7, R.id.grid_9, R.id.grid_11, R.id.grid_13, R.id.grid_15)
    }
    fun makeHorizontalStripe(color1: Color, color2: Color) {
        setColorForButtons(color1, R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3, R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11)
        setColorForButtons(color2, R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7, R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15)
    }
    fun makeMonochrome(color1: Color) {
        setColorForButtons(color1, padIds.toSet())
    }
}
