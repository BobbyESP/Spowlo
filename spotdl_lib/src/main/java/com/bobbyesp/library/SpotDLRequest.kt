package com.bobbyesp.library

import android.util.Log

open class SpotDLRequest(url: String? = null, urls: List<String>? = null) {

    companion object {
        fun getInstance(): SpotDLUpdater {
            return SpotDLUpdater()
        }
    }

    private var urls: List<String> = listOf()
    private var options = SpotDLOptions()
    private var customCommandList = ArrayList<String>()

    open fun addOption(option: String, argument: String): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    open fun addOption(option: String, argument: Number): SpotDLRequest {
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
        if(BuildConfig.DEBUG) Log.d("SpotDLRequest", urls.toString())
        if(BuildConfig.DEBUG) Log.d("SpotDLRequest", "Commands: $finalCommandList")
        return finalCommandList
    }

}