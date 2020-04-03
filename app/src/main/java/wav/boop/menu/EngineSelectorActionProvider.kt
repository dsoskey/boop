package wav.boop.menu

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.ActionProvider
import wav.boop.R

class EngineSelectorActionProvider (context: Context) : ActionProvider(context), AdapterView.OnItemSelectedListener {
    private val engines: List<String> = listOf("sin", "square", "saw")

    private external fun setWaveform(waveformGenerator: String)

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        setWaveform(adapterView?.getItemAtPosition(pos).toString())
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}

    override fun onCreateActionView(): View {
        val inflater = LayoutInflater.from(context)
        val providerView = inflater.inflate(R.layout.pad_pattern_selector, null)

        setWaveform("sin")
        val spinner = providerView!!.findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, engines)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        return providerView
    }
}