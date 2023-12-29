package com.bobbyesp.utilities.utilities

import android.content.Context
import android.widget.Toast

object Toast {
    fun makeToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun makeToast(context: Context, stringId: Int) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show()
    }
}