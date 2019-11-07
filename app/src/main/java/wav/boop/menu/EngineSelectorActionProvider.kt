package wav.boop.menu

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.ActionProvider
import wav.boop.BoopApp
import wav.boop.R
import wav.boop.synth.DefaultSynthesizer
import wav.boop.waveform.SawEngine
import wav.boop.waveform.SineEngine
import wav.boop.waveform.SquareEngine
import javax.inject.Inject

class EngineSelectorActionProvider (context: Context) : ActionProvider(context), AdapterView.OnItemSelectedListener {
    enum class Engines {
        SINE,
        SAW,
        SQUARE,
    }

    @Inject lateinit var synthesizer: DefaultSynthesizer

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val newEngine = when (
            Engines.valueOf(adapterView?.getItemAtPosition(pos).toString())
        ) {
            Engines.SAW -> SawEngine(100)
            Engines.SINE -> SineEngine()
            Engines.SQUARE -> SquareEngine()
        }
        synthesizer.waveformEngine = newEngine
    }
    override fun onNothingSelected(adapterView: AdapterView<*>?) {}

    override fun onCreateActionView(): View {
        (context.applicationContext as BoopApp).appGraph.inject(this)
        val inflater = LayoutInflater.from(context)
        val providerView = inflater.inflate(R.layout.pad_pattern_selector, null)

        val options = listOf(*Engines.values())
        val spinner = providerView!!.findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        return providerView
    }
}