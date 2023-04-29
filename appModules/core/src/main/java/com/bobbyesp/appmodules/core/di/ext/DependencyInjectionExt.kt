package com.bobbyesp.appmodules.core.di.ext

import okhttp3.OkHttpClient
import okhttp3.Request

object DependencyInjectionExt {
    fun OkHttpClient.Builder.interceptRequest(scope: Request.Builder.(Request) -> Unit) = addInterceptor { chain ->
        val request = chain.request()
        chain.proceed(request.newBuilder().apply { scope(this, request) }.build())
    }
}