package com.bobbyesp.appmodules.core.utils

import retrofit2.Retrofit
import retrofit2.create

inline fun <reified T> Retrofit.create(baseUrl: String) = newBuilder().baseUrl(baseUrl).build().create<T>()