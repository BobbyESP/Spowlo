package com.bobbyesp.spotdl_android.data

import android.util.Log
import com.bobbyesp.spotdl_android.BuildConfig

class SpotDLRequest {
    private var urls: List<String> = listOf()
    private var options = SpotDLOptions()
    private var customCommandList = ArrayList<String>()

    constructor(url: String) {
        urls = listOf(url)
    }

    constructor(urls: List<String>) {
        this.urls = urls
    }

    fun addOption(option: String, argument: String): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    fun addOption(option: String, argument: Number): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }


    fun addOption(option: String): SpotDLRequest {
        options.addOption(option)
        return this
    }

    fun addCommands(commands: List<String>): SpotDLRequest {
        customCommandList.addAll(commands)
        return this
    }


    fun getOption(option: String): String {
        return options.getArgument(option) ?: ""
    }

    fun getArguments(option: String): List<String> {
        return options.getArguments(option) ?: listOf()
    }

    fun hasOption(option: String): Boolean {
        return options.hasOption(option)
    }

    fun buildCommand(): List<String> {
        val finalCommandList = ArrayList<String>()
        finalCommandList.addAll(options.buildOptions())
        finalCommandList.addAll(urls)
        if (BuildConfig.DEBUG) Log.i("SpotDLRequest", urls.toString())
        if (BuildConfig.DEBUG) {
            Log.i("SpotDLRequest", "Commands: $finalCommandList")

            for ((numberOfArguments, command) in finalCommandList.withIndex()) {
                Log.i("SpotDLRequest", "Index: $numberOfArguments -> $command")
            }

        }
        return finalCommandList
    }
}