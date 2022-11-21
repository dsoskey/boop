import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.compose.rememberNavController
import wav.boop.model.SynthesizerModel
import wav.boop.pad.getOscillatorsForIsoKey
import wav.boop.ui.LockScreenOrientation
import wav.boop.ui.component.HexButton
import wav.boop.ui.component.HexagonShape
import wav.boop.ui.component.IsomorphicKeyboard
import wav.boop.ui.theme.BoopTheme

enum class AppRoute(val path: String) {
    HOME("/")

}

@Composable
fun App(synthModel: SynthesizerModel) {
    val buttonSize = LocalConfiguration.current.screenWidthDp.dp / 9f
    val menuOpen = remember { false }
    val navController = rememberNavController()

    BoopTheme {

        LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (topStart, topEnd, bottomRow) = createRefs()

            val keyboardTop = createGuidelineFromTop(buttonSize * .25f)

            Row(modifier = Modifier
                .offset(0.dp, -buttonSize / 2)
                .constrainAs(topStart) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }) {
                HexButton(
                    hypotenuseLength = buttonSize,
                    orientation = HexagonShape.Orientation.VERTICAL) {
                    Text("Synth")
                }
                HexButton(
                    hypotenuseLength = buttonSize,
                    orientation = HexagonShape.Orientation.VERTICAL) {
                    Text("Sample")
                }
            }
            Row(modifier = Modifier
                .offset(0.dp, -buttonSize / 2)
                .constrainAs(topEnd) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }) {
                HexButton(
                    hypotenuseLength = buttonSize,
                    orientation = HexagonShape.Orientation.VERTICAL) {
                    Text("Patch")
                }
                HexButton(
                    hypotenuseLength = buttonSize,
                    orientation = HexagonShape.Orientation.VERTICAL) {
                    Text("Color")
                }
            }
            IsomorphicKeyboard(
                keySize = buttonSize,
                numOctaves = 2,
                showNote = true,
                showOctave = true,
                showBottomRow = true,
                onPress = { keyIndex -> getOscillatorsForIsoKey(keyIndex).forEach {
                    Log.v("app", "oscillator ${it}_$keyIndex on")
                    synthModel.setWaveOn(it, isOn = true)
                }},
                onRelease = { keyIndex -> getOscillatorsForIsoKey(keyIndex).forEach {
                    Log.v("app", "oscillator ${it}_$keyIndex off")
                    synthModel.setWaveOn(it, isOn = false)
                }},
                modifier = Modifier.constrainAs(bottomRow) {
                    top.linkTo(keyboardTop)
                }
            )
        }
    }
}