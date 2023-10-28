package com.bobbyesp.commonutilities

import android.util.Log

object CommonUtilities {
    fun getInstance() = this

    fun init() {
        Log.i("CommonUtilities", "Initialized the library")
    }
}