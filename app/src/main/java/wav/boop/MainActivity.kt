package wav.boop

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import wav.boop.color.getThemeColor
import wav.boop.file.buildPresetLoader
import wav.boop.model.*
import wav.boop.model.SynthesizerModel
import wav.boop.model.SynthesizerModel.Companion.AUTOSAVE_PREFIX
import wav.boop.pitch.Scale

const val BOOP_REQUEST_CODE = 0
/**
 * Root activity for boop.
 */
class MainActivity : AppCompatActivity() {
    // Native interface for AudioEngine controls
    private external fun startEngine(cpuIds: IntArray)
    private external fun isEngineRunning(): Boolean
    private external fun stopEngine()
    private external fun setDefaultStreamValues(sampleRate: Int, framesPerBurst: Int)

    lateinit var colorScheme: ColorScheme
    lateinit var synthModel: SynthesizerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isRecordPermissionGranted()) {
            requestRecordPermission()
        }
        if (!isEngineRunning()) {
            startEngine(getExclusiveCores())
            setDefaultStreamValues()
        }

        val synthFactory = SynthesizerModelFactory(
            buildPresetLoader(applicationContext),
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

        synthModel.loadPreset(AUTOSAVE_PREFIX)
    }

    override fun onStop() {
        synthModel.saveCurrentPreset(AUTOSAVE_PREFIX)
        super.onStop()
    }

    override fun onRestart() {
        if (!isEngineRunning()) {
            startEngine(getExclusiveCores())
            synthModel.refresh()
        }
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

    private fun requestRecordPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), BOOP_REQUEST_CODE)
    }

    private fun isRecordPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}