package com.bobbyesp.spotdl_android.data

/**
 * Options for the SpotDL process.
 */
open class SpotDLOptions {

    private val options: MutableMap<String, List<String>> = LinkedHashMap()

    private fun getOrCreateArgumentList(option: String): MutableList<String> {
        return options.getOrPut(option) { mutableListOf() } as MutableList<String>
    }

    open fun addOption(option: String, argument: String): SpotDLOptions {
        getOrCreateArgumentList(option).add(argument)
        return this
    }

    open fun addOption(option: String, argument: Number): SpotDLOptions {
        getOrCreateArgumentList(option).add(argument.toString())
        return this
    }

    open fun addOption(option: String): SpotDLOptions {
        getOrCreateArgumentList(option).add("")
        return this
    }

    open fun getArgument(option: String): String? {
        return options[option]?.get(0)?.ifEmpty { null }
    }

    open fun getArguments(option: String): List<String>? {
        return options[option]
    }

    open fun hasOption(option: String?): Boolean {
        return option != null && option in options
    }

    open fun buildOptions(): List<String> {
        return options.flatMap { (option, arguments) ->
            arguments.flatMap { argument ->
                listOf(option, argument).filter { it.isNotEmpty() }
            }
        }
    }
}