package com.bobbyesp.utilities.utilities

import kotlin.random.Random

object StringUtils {
    fun generateRandomString(length: Int, useLetters: Boolean, useNumbers: Boolean): String {
        val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val characters = mutableListOf<Char>()

        if (useLetters) {
            characters.addAll(letters.toList())
        }

        if (useNumbers) {
            characters.addAll(numbers.toList())
        }

        if (characters.isEmpty()) {
            throw IllegalArgumentException("At least one of useLetters or useNumbers must be true")
        }

        return (1..length)
            .map { characters[Random.nextInt(characters.size)] }
            .joinToString("")
    }
}