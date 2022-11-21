package wav.boop.ui.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import wav.boop.ui.theme.BoopTheme

// TODO: Try to make this an enum class
class HexagonShape(
    val orientation: Orientation,
) : Shape {
    // Orientation of hypotenuse
    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
        when(orientation) {
            Orientation.VERTICAL -> Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(size.width, size.height / 4)
                lineTo(size.width, size.height * .75f)
                lineTo(size.width / 2, size.height)
                lineTo(0f, size.height * .75f)
                lineTo(0f, size.height / 4)
                close()
            }
            Orientation.HORIZONTAL -> Path().apply {
                moveTo(0f, size.height / 2)
                lineTo(size.width / 4, size.height)
                lineTo(size.width * .75f, size.height)
                lineTo(size.width, size.height / 2)
                lineTo(size.width * .75f, 0f)
                lineTo(size.width / 4, 0f)
                close()
            }
        })
    }
}

@Composable
fun HexButton(
    hypotenuseLength: Dp,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    orientation: HexagonShape.Orientation,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .size(hypotenuseLength)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        /* Called when the gesture starts */
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    },
                )
            },
        shape = HexagonShape(orientation = orientation),
        color = colors.backgroundColor(enabled = true).value,
        contentColor = colors.contentColor(enabled = true).value,
    ) {
        Row(
            Modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BoopTheme {
        Row {
            HexButton(hypotenuseLength = 48.dp, orientation = HexagonShape.Orientation.HORIZONTAL) {
                Text("C")
            }
            HexButton(hypotenuseLength = 48.dp, orientation = HexagonShape.Orientation.VERTICAL) {
                Text("441")
            }
        }
    }
}