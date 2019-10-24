package com.example.boop.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ActionProvider
import com.example.boop.R

class ColorPickerActionProvider(context: Context) : ActionProvider(context) {
    override fun onCreateActionView(): View {
        val inflater = LayoutInflater.from(context)
        val providerView = inflater.inflate(R.layout.pad_color_selector, null)
        return providerView
    }
}