package com.bobbyesp.spowlo.utils

import androidx.annotation.CheckResult
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.SpotDLResponse
import com.bobbyesp.library.dto.Song
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID

object DownloadsUtil {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }

    //Get a random UUID and return it as a string
    private fun getRandomUUID(): String {
        return UUID.randomUUID().toString()
    }

    @CheckResult
    private fun getVideoInfo(request: SpotDLRequest, id: String = getRandomUUID()): Result<List<Song>> =
        request.addOption("--save-file", "$id.spotdl").runCatching {
            val response: SpotDLResponse = SpotDL.getInstance().execute(request, null, null)
            jsonFormat.decodeFromString(response.output)
        }
}