package com.bobbyesp.spowlo.util

import android.widget.Toast
import com.bobbyesp.spowlo.Spowlo.Companion.context

object Utils {
    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}