package com.gmail.ortuchr.finance.presentation.utils

import android.content.Context
import android.graphics.Typeface
import android.util.Log


class TypefaceUtil {
    companion object {
        fun overrideFont(context: Context, defaultFontNameToOverride: String, customFontFileNameInAssets: String) {
            try {
                val customFontTypeface = Typeface.createFromAsset(context.assets, customFontFileNameInAssets)
                val defaultFontTypefaceField = Typeface::class.java.getDeclaredField(defaultFontNameToOverride)
                defaultFontTypefaceField.isAccessible = true
                defaultFontTypefaceField.set(null, customFontTypeface)
            } catch (e: Exception) {
                Log.e("FontError", "Can not set custom font $customFontFileNameInAssets instead of $defaultFontNameToOverride")
            }
        }
    }
}