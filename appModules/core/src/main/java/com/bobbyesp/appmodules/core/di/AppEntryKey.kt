package com.bobbyesp.appmodules.core.di

import com.bobbyesp.appmodules.core.AppEntry
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class AppEntryKey(
    val value: KClass<out AppEntry>
)