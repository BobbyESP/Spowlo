package com.bobbyesp.spowlo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class Spowlo : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    companion object{
        lateinit var applicationScope: CoroutineScope

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}