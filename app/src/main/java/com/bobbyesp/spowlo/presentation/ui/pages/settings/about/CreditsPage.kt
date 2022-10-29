package com.bobbyesp.spowlo.presentation.ui.pages.settings.about

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState

data class Credit(val title: String = "", val license: String? = null, val url: String = "")

//Licenses
const val GPL_V3 = "GNU General Public License v3.0"
const val GPL_V2 = "GNU General Public License v2.0"
const val APACHE_V2 = "Apache License, Version 2.0"
const val UNLICENSE = "The Unlicense"
const val BSD = "BSD 3-Clause License"

//Used repos and libraries
