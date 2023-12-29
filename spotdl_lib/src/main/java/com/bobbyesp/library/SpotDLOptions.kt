package com.bobbyesp.library

open class SpotDLOptions {

    private val options: MutableMap<String, MutableList<String>> = LinkedHashMap()

    open fun addOption(option: String, argument: String): SpotDLOptions {
        if (!options.containsKey(option)) {
            val arguments = ArrayList<String>()
            arguments.add(argument)
            options[option] = arguments
        } else {
            options[option]?.add(argument)
        }
        return this
    }


    open fun addOption( option: String, argument: Number): SpotDLOptions {
        if (!options.containsKey(option)) {
            val arguments = ArrayList<String>()
            arguments.add(argument.toString())
            options[option] = arguments
        } else {
            options[option]?.add(argument.toString())
        }
        return this
    }

    open fun addOption(option: String): SpotDLOptions {
        if (!options.containsKey(option)) {
            val arguments = ArrayList<String>()
            arguments.add("")
            options[option] = arguments
        } else {
            options[option]?.add("")
        }
        return this
    }


    open fun getArgument(option: String): String? {
        if (!options.containsKey(option)) return null
        val argument = options[option]?.get(0) ?: return null
        return if (argument.isEmpty()) null else argument
    }


    open fun getArguments(option: String): List<String>? {
        return if (!options.containsKey(option)) null else options[option]
    }

    open fun hasOption(option: String?): Boolean {
        return options.containsKey(option)
    }

    open fun buildOptions(): List<String> {
        val commandList = ArrayList<String>()
        for (entry in options.entries) {
            val option = entry.key
            for (argument in entry.value) {
                commandList.add(option)
                if (argument.isNotEmpty()) {
                    commandList.add(argument)
                }
            }
        }
        return commandList
    }
}