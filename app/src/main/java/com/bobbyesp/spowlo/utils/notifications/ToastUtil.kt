package com.bobbyesp.spowlo.utils.notifications

import android.content.Context
import android.widget.Toast
import com.bobbyesp.spowlo.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastUtil {
    fun makeToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun makeToastSuspend(context: Context, text: String) {
        App.applicationScope.launch(Dispatchers.Main) {
            makeToast(context, text)
        }
    }

    fun makeToast(context: Context, stringId: Int) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show()
    }
}