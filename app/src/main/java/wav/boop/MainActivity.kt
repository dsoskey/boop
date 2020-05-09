package wav.boop

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import wav.boop.color.getThemeColor
import wav.boop.control.ControlFragment
import wav.boop.control.colorButtonIds
import wav.boop.menu.TitleBarFragment
import wav.boop.model.ColorAssignment
import wav.boop.model.ColorScheme
import wav.boop.model.PitchContainer
import wav.boop.pad.PadFragment
import wav.boop.pad.padIds
import wav.boop.pitch.Scale

/**
 * Root activity for boop.
 */
class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
    // Native interface for AudioEngine controls
    private external fun startEngine(cpuIds: IntArray)
    private external fun stopEngine()
    private external fun setDefaultStreamValues(sampleRate: Int, framesPerBurst: Int)

    // ColorPicker Dialog management
    lateinit var colorScheme: ColorScheme
    override fun onDialogDismissed(dialogId: Int) {}
    override fun onColorSelected(dialogId: Int, colorInt: Int) {
        val color = Color.valueOf(colorInt)
        when {
            padIds.contains(dialogId) -> {
                colorScheme.setColorForButtons(color, dialogId)
            }
            colorButtonIds.contains(dialogId) -> {
                val assignment: ColorAssignment? = colorScheme.getAssignment(colorButtonIds.indexOf(dialogId))
                if (assignment != null) {
                    colorScheme.setColorForButtons(color, assignment.padIds)
                }
            }
            else -> error("Dialog ID (${dialogId}) not recognized as either a pad or a color button")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startEngine(getExclusiveCores())
        setDefaultStreamValues()

        colorScheme = ViewModelProvider(this)[ColorScheme::class.java]
        colorScheme.makeMode(
            Color.valueOf(getThemeColor(theme, R.attr.colorAccent)),
            Color.valueOf(getThemeColor(theme, R.attr.colorOnPrimary)),
            Scale.IONIAN
        )

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val padFragment = PadFragment()
        fragmentTransaction.add(R.id.main_action, padFragment)
        fragmentTransaction.commit()

        val controlTransaction = supportFragmentManager.beginTransaction()
        val controlFragment = ControlFragment()
        controlTransaction.add(R.id.side_action, controlFragment)
        controlTransaction.commit()

        val titleBarTransaction = supportFragmentManager.beginTransaction()
        val titleBarFragment = TitleBarFragment()
        titleBarTransaction.add(R.id.title_bar, titleBarFragment)
        titleBarTransaction.commit()
    }

    override fun onStop() {
        stopEngine()
        super.onStop()
    }

    override fun onRestart() {
        val pitchContainer = ViewModelProvider(this)[PitchContainer::class.java]
        startEngine(getExclusiveCores())
        // TODO: Restore all settings stored in ViewModels
        pitchContainer.setTonic(pitchContainer.tonicFrequency)
        super.onRestart()
    }

    private fun getExclusiveCores(): IntArray {
        var exclusiveCores: IntArray = intArrayOf()

        try {
            exclusiveCores = Process.getExclusiveCores()
        } catch (e: RuntimeException) {
            println("getExclusiveCores() not supported on this device")
        }
        return exclusiveCores
    }

    private fun setDefaultStreamValues() {
        val myAudioMgr = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        val defaultSampleRate = sampleRateStr.toInt()
        val framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        val defaultFramesPerBurst = framesPerBurstStr.toInt()
        setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst)
    }

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}