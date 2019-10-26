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

class PadPatternSelector(private val colorSchemeManager: ColorSchemeManager) :
    Fragment(), AdapterView.OnItemSelectedListener {

    private var fragmentView: View? = null

    private enum class PadPattern {
        DIAGONAL,
        CHECKERED,
        PIANO,
        HORIZONTAL_STRIPED,
        MONOCHROME
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = PadPattern.valueOf(parent.getItemAtPosition(pos).toString())
        val color1 = Color.valueOf(R.attr.colorPrimaryDark)
        val color2 = Color.valueOf(R.attr.colorAccent)
        val scheme: ColorScheme = when (selectedItem) {
            PadPattern.DIAGONAL -> ColorScheme.diagonal(color1, color2) // Something about the gradient map is causing a crash when the second color gets changed
            PadPattern.CHECKERED -> ColorScheme.checkered(color1, color2)
            PadPattern.HORIZONTAL_STRIPED -> ColorScheme.horizontalStriped(color1, color2)
            PadPattern.PIANO -> ColorScheme.piano(color1, color2)
            PadPattern.MONOCHROME -> ColorScheme.monochrome(color1)
        }
        colorSchemeManager.onColorSchemeChange(scheme)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.pad_pattern_selector, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureColorScheme()
    }

    private fun configureColorScheme() {
        val options = listOf(*PadPattern.values())
        val spinner = fragmentView!!.findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }
}
