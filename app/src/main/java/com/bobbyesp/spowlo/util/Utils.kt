package com.bobbyesp.spowlo.util

import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.bobbyesp.spowlo.Spowlo.Companion.applicationScope
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.util.Utils.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Utils {

    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
    fun makeToastSuspend(text: String) {
        applicationScope.launch(Dispatchers.Main) {
            makeToast(text)
        }
    }
}
