package com.bobbyesp.spowlo.data.remote

class ApiHelperImpl (private val apiService: xManagerAPI) : APIHelper {
    override suspend fun getAPIInfo() = apiService.getAPIInfo()
}