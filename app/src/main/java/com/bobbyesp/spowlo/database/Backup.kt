package com.bobbyesp.spowlo.database

import kotlinx.serialization.Serializable

@Serializable
data class Backup(val templates: List<CommandTemplate>, val shortcuts: List<CommandShortcut>)