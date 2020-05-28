package wav.boop

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.Process
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import wav.boop.color.getThemeColor
import wav.boop.control.ControlFragment
import wav.boop.control.colorButtonIds
import wav.boop.menu.TitleBarFragment
import wav.boop.model.*
import wav.boop.pad.PadFragment
import wav.boop.pad.padIds
import wav.boop.pitch.Scale
import wav.boop.preset.DefaultPresetLoader
import wav.boop.model.SynthesizerModel
import wav.boop.model.SynthesizerModel.Companion.AUTOSAVE_PREFIX
import wav.boop.model.SynthesizerModelFactory
import wav.boop.pad.TestPad

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

    lateinit var controlFragment: ControlFragment

    lateinit var synthModel: SynthesizerModel
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

        setSupportActionBar(findViewById(R.id.title_bar))
        title_bar.setNavigationOnClickListener {
            drawer_layout.openDrawer(Gravity.LEFT)
        }

        startEngine(getExclusiveCores())
        setDefaultStreamValues()

        val synthFactory = SynthesizerModelFactory(
            DefaultPresetLoader(applicationContext, Json(JsonConfiguration.Stable)),
            ViewModelProvider(this)[PitchModel::class.java],
            ViewModelProvider(this)[ADSRModel::class.java],
            ViewModelProvider(this)[OscillatorModel::class.java]
        )
        synthModel = ViewModelProvider(this, synthFactory)[SynthesizerModel::class.java]

        colorScheme = ViewModelProvider(this)[ColorScheme::class.java]
        colorScheme.makeMode(
            Color.valueOf(getThemeColor(theme, R.attr.colorAccent)),
            Color.valueOf(getThemeColor(theme, R.attr.colorOnPrimary)),
            Scale.IONIAN
        )

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val padFragment = PadFragment()
//        val padFragment = TestPad()
        fragmentTransaction.add(R.id.main_action, padFragment)
        fragmentTransaction.commit()

        val controlTransaction = supportFragmentManager.beginTransaction()
        controlFragment = ControlFragment()
        controlTransaction.add(R.id.side_action, controlFragment)
        controlTransaction.commit()

        app_navigation.setNavigationItemSelectedListener { item ->
            val index = when (item.itemId) {
                R.id.preset_menu_option -> 0
                R.id.pitch_menu_option -> 1
                R.id.oscillator_menu_option -> 2
                R.id.adsr_menu_option -> 3
                R.id.pad_color_option -> 4
                else -> 0
            }
            controlFragment.setPage(index)
            drawer_layout.closeDrawer(Gravity.LEFT)
            true
        }

//        val titleBarTransaction = supportFragmentManager.beginTransaction()
//        val titleBarFragment = TitleBarFragment()
//        titleBarTransaction.add(R.id.title_bar, titleBarFragment)
//        titleBarTransaction.commit()

        synthModel.loadPreset(AUTOSAVE_PREFIX)
    }

    override fun onStop() {
        synthModel.saveCurrentPreset(AUTOSAVE_PREFIX)
        stopEngine()
        super.onStop()
    }

    override fun onRestart() {
        startEngine(getExclusiveCores())
        synthModel.refresh()
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