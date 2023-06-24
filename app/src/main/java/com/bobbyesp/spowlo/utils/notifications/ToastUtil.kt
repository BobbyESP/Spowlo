package com.bobbyesp.spowlo.utils.notifications

import android.widget.Toast
import com.bobbyesp.spowlo.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastUtil {
    fun makeToast(text: String) {
        Toast.makeText(App.context, text, Toast.LENGTH_SHORT).show()
    }

    fun makeToastSuspend(text: String) {
        App.applicationScope.launch(Dispatchers.Main) {
            makeToast(text)
        }
    }

    fun makeToast(stringId: Int) {
        Toast.makeText(App.context, App.context.getString(stringId), Toast.LENGTH_SHORT).show()
    }
}