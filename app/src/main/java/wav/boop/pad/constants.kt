package wav.boop.pad

import wav.boop.R

val padIds: IntArray = intArrayOf(
    R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3,
    R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7,
    R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11,
    R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15
)

fun getOscillatorsForPad(padId: Int, numVoices: Int = 2): IntArray {
    val index = padIds.indexOf(padId)
    if (index == -1) {
        return intArrayOf()
    }
    return (0 until numVoices).map { osc -> osc + index * numVoices  }.toIntArray()
}

val padToOscillator: Map<Int, IntArray> = padIds.associate { it to getOscillatorsForPad(it) }