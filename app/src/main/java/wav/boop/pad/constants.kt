package wav.boop.pad

import wav.boop.R

/**
 * 4x4 pad ids.
 */
val padIds: IntArray = intArrayOf(
    R.id.grid_0, R.id.grid_1, R.id.grid_2, R.id.grid_3,
    R.id.grid_4, R.id.grid_5, R.id.grid_6, R.id.grid_7,
    R.id.grid_8, R.id.grid_9, R.id.grid_10, R.id.grid_11,
    R.id.grid_12, R.id.grid_13, R.id.grid_14, R.id.grid_15
)
/**
 * This should fill [8,39] as defined in [cpp/core/Synth.h]
 */
fun getOscillatorsForPad(padId: Int, numVoices: Int = 2): IntArray {
    val index = padIds.indexOf(padId)
    if (index == -1) {
        return intArrayOf()
    }
    return (0 until numVoices).map { osc -> 8 + osc + index * numVoices }.toIntArray()
}
val padToOscillator: Map<Int, IntArray> = padIds.associate { it to getOscillatorsForPad(it, 2) }

/**
 * isomorphic pad index
 */
fun getOscillatorsForIsoKey(keyIndex: Int, numVoices: Int = 2): IntArray {
    return (0 until numVoices).map { oscillatorIndex -> oscillatorIndex + keyIndex * numVoices }.toIntArray()
}