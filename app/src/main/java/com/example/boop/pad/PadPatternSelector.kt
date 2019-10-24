package com.example.boop.pad

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.boop.R
import java.util.*

class PadPatternSelector(private val colorSchemeManager: ColorSchemeManager, private val buttonToColor: Map<Int, Int>) :
    Fragment(), AdapterView.OnItemSelectedListener {

    private var fragmentView: View? = null

    private enum class PadPattern {
        DIAGONAL,
        CHECKERED,
        PIANO,
        HORIZONTAL_STRIPED
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = PadPattern.valueOf(parent.getItemAtPosition(pos).toString())
        val color1 = Color.valueOf(R.attr.colorPrimaryDark) // TODO: Handle which color is which when selecting. Within a ColorScheme Colors need an ordering and a configurable size (16 under the hood but abstracted away through themes. One theme would have 16 colors!
        val color2 = Color.valueOf(R.attr.colorAccent)
        val scheme: ColorScheme = when (selectedItem) {
            PadPattern.DIAGONAL -> ColorScheme.DIAGONAL(color1, color2) // Something about the gradient map is causing a crash when the second color gets changed
            PadPattern.CHECKERED -> ColorScheme.CHECKERED(color1, color2)
            PadPattern.HORIZONTAL_STRIPED -> ColorScheme.HORIZONTAL_STRIPED(color1, color2)
            PadPattern.PIANO -> ColorScheme.PIANO(color1, color2)
        }
        colorSchemeManager.onColorSchemeChange(scheme)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.pad_pattern_selector, container, false) // TODO: Separate layout
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureColorScheme()
    }

    private fun configureColorScheme() {
        val options = Arrays.asList(*PadPattern.values())
        val spinner = fragmentView!!.findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setOnItemSelectedListener(this)
    }
}
