package com.gmail.ortuchr.finance.presentation.utils

import android.view.View
import androidx.databinding.BindingConversion

@BindingConversion
fun convertBooleanToVisibility(visible: Boolean) : Int {
    return when (visible) {
        true -> View.VISIBLE
        else -> View.GONE
    }
}
