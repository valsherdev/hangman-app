package com.example.hangman

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class HangmanViewModel(application: Application) : AndroidViewModel(application) {


    private val words: List<String> = loadWords()
    var targetWord by mutableStateOf(words.random())

    val maxLives = 6
    var usedLives by mutableStateOf(0)
        private set

    var guessedLetters by mutableStateOf(setOf<Char>())
        private set


    // loading words from raw resource
    private fun loadWords(): List<String> {
        val inputStream = getApplication<Application>().resources.openRawResource(R.raw.words)
        return inputStream.bufferedReader().readLines()
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
    }

    fun getDisplayWord(): String {
        return targetWord
            .map {letter -> if (letter in guessedLetters) letter else '_'}
            .joinToString(" ")
    }


    fun getLivesRemaining(): Int {
        return maxLives - usedLives
    }

    fun isWon(): Boolean {
        return targetWord.all { letter -> letter in guessedLetters }
    }

    fun isLost(): Boolean {
        return getLivesRemaining() == 0
    }

    fun isGameOver(): Boolean {
        return isWon() || isLost()
    }

    fun guessLetter(letter: Char) {
        if (isGameOver()) return

        val guess = letter.lowercaseChar()
        if (guess in guessedLetters) return

        guessedLetters = guessedLetters + guess

        if (guess !in targetWord) {
            usedLives++
        }
    }

    fun restart() {
        targetWord = words.random()
        guessedLetters = setOf<Char>()
        usedLives = 0
    }


    fun letterStatus(letter: Char): String {
        if (letter !in guessedLetters) return "unguessed"
        return if (letter in targetWord) "correct" else "wrong"
    }
}