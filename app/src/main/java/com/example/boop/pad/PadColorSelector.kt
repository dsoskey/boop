package com.example.boop.pad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.boop.R

import com.jaredrummler.android.colorpicker.ColorPickerDialog

class PadColorSelector(private val parent: FragmentActivity, private val buttonToColor: Map<Int, Int>) : Fragment(),
    View.OnClickListener {

    private var fragmentView: View? = null

    override fun onClick(view: View) {
        val buttonId = view.id
        val colorInt = buttonToColor.getOrDefault(buttonId, 0)
        ColorPickerDialog.newBuilder()
            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
            .setAllowPresets(false)
            .setDialogId(buttonId)
            .setColor(colorInt)
            .setShowAlphaSlider(true)
            .show(parent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.pad_color_selector, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureColorPicker()
    }

    private fun configureColorPicker() {
        val button1 = fragmentView!!.findViewById<Button>(R.id.colorpicker1)
        button1.setOnClickListener(this)
        val button2 = fragmentView!!.findViewById<Button>(R.id.colorpicker2)
        button2.setOnClickListener(this)
    }


}
