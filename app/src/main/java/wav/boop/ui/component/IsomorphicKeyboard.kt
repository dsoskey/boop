package wav.boop.ui.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import wav.boop.ui.theme.BoopTheme

data class RowInfo(
    val notes: List<String>,
    // Indexes of natural notes in row. Indexes not listed are considered accidental.
    val naturalNotes: List<Int>,
)

@Composable
fun IsomorphicTopRow(
    keySize: Dp = 64.dp,
    showNote: Boolean = false,
    orientation: HexagonShape.Orientation = HexagonShape.Orientation.VERTICAL,
    naturalNoteColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = MaterialTheme.colors.secondary,
    ),
    accidentalNoteColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.primaryVariant,
    ),
    // keyIndex of first button in the row
    onPress: (keyIndex: Int) -> Unit,
    onRelease: (keyIndex: Int) -> Unit,
    startingKeyIndex: Int,
) {
    val rowInfo = RowInfo(
        listOf("D♭", "E♭", "F", "G", "A" , "B", "C♯", "D♯"),
        listOf(2, 3, 4, 5),
    )
    Log.e("top row", "$startingKeyIndex")

    Row(
        modifier = Modifier
            .padding(start = keySize / 2)
    ) {
        for (i in 1..8) {
            HexButton(
                hypotenuseLength = keySize,
                orientation = orientation,
//                modifier = Modifier.padding(start = if (i > 1) 3.dp else 1.5.dp),
                colors = if (rowInfo.naturalNotes.contains(i-1)) naturalNoteColor else accidentalNoteColor,
                onPress = {
                    Log.e("top row", "pressed ${rowInfo.notes[i-1]}")
                    onPress(startingKeyIndex + i - 1)
              },
                onRelease = { onRelease(startingKeyIndex + i - 1) },
            ) {
                if (showNote) Text(rowInfo.notes[i - 1])
            }
        }
    }
}

@Composable
fun IsomorphicBottomRow(
    keySize: Dp = 64.dp,
    showNote: Boolean = false,
    orientation: HexagonShape.Orientation = HexagonShape.Orientation.VERTICAL,
    naturalNoteColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = MaterialTheme.colors.secondary,
    ),
    accidentalNoteColor: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.primaryVariant,
    ),
    octave: Int? = null,
    showOctave: Boolean = false,
    onPress: (keyIndex: Int) -> Unit,
    onRelease: (keyIndex: Int) -> Unit,
    // keyIndex of first button in the row
    startingKeyIndex: Int,
) {
    val rowInfo = RowInfo(
        listOf("G♭", "A♭", "B♭", "C", "D", "E", "F♯", "G♯", "A♯"),
        listOf(3, 4, 5),
    )

    Row {
        for (i in 1..9) {
            HexButton(
                hypotenuseLength = keySize,
                orientation = orientation,
//                modifier = Modifier.padding(start = if (i > 1) 3.dp else 1.5.dp),
                colors = if (rowInfo.naturalNotes.contains(i-1)) naturalNoteColor else accidentalNoteColor,
                onPress = {
                    Log.e("bottom row", "pressed ${rowInfo.notes[i-1]}${octave}")
                    onPress(startingKeyIndex + i - 1)
                },
                onRelease = { onRelease(startingKeyIndex + i - 1) },
            ) {
                if (showNote) Text(rowInfo.notes[i - 1])
                if (octave != null && showOctave && i == 4) Text(octave.toString())
            }
        }
    }
}

@Composable
fun IsomorphicKeyboard(
    modifier: Modifier = Modifier,
    numOctaves: Int = 2,
    initialOctave: Int = 4,
    keySize: Dp = 64.dp,
    showNote: Boolean = false,
    showOctave: Boolean = false,
    // adds a half octave below the bottom octave
    showBottomRow: Boolean = false,
    onPress: (keyIndex: Int) -> Unit = {},
    onRelease: (keyIndex: Int) -> Unit = {},
) {
//    TODO: Validate numOctives >= 1
    val offsetBase = 1.5f
    ConstraintLayout(modifier = modifier) {
        for (i in numOctaves downTo 1) {
            val (topRow, bottomRow) = createRefs()
            val octaveIndex = numOctaves - i // 0..length-1

            val topGuideline = createGuidelineFromTop(keySize * octaveIndex * offsetBase)

            val bottomGuideline = createGuidelineFromTop(keySize * octaveIndex * offsetBase + keySize * .75f)

            Column(
                modifier = Modifier.constrainAs(topRow) {
                    top.linkTo(topGuideline)
                }
            ) {
                IsomorphicTopRow(
                    keySize = keySize,
                    showNote = showNote,
                    onPress = onPress,
                    onRelease = onRelease,
                    startingKeyIndex = (i-1) * 17 + 17
                )
            }
            Column(
                modifier = Modifier.constrainAs(bottomRow) {
                    top.linkTo(bottomGuideline)
                }
            ) {
                IsomorphicBottomRow(
                    keySize = keySize,
                    showNote = showNote,
                    showOctave = showOctave,
                    octave = initialOctave + i - 1,
                    onPress = onPress,
                    onRelease = onRelease,
                    startingKeyIndex = (i-1) * 17 + 8
                )
            }
        }
        if (showBottomRow) {
            val lastGuideline = createGuidelineFromTop(keySize * numOctaves * offsetBase)
            val (lastRow) = createRefs()
            Column(
                modifier = Modifier.constrainAs(lastRow) {
                    top.linkTo(lastGuideline)
                }

            ) {
                IsomorphicTopRow(
                    keySize = keySize,
                    showNote = showNote,
                    onPress = onPress,
                    onRelease = onRelease,
                    startingKeyIndex = 0,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TwoOctavePreview() {
    BoopTheme(darkTheme = true) {
        Column {
            IsomorphicKeyboard(
                numOctaves = 2,
                showNote = true,
                showOctave = true,
                showBottomRow = true,
            )
            Text("Isomorphic keyboard!")
        }
    }
}
