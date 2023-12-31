package com.bobbyesp.utilities.audio.model

enum class LibraryViewType {
    LIST, GRID;

    fun toggle() = when (this) {
        LIST -> GRID
        GRID -> LIST
    }
}

