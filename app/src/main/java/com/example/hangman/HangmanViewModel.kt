package com.example.hangman

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HangmanViewModel: ViewModel() {

    var targetWord by mutableStateOf(hangmanWords.random())

    val maxLives = 6
    var usedLives by mutableStateOf(0)
        private set

    var guessedLetters by mutableStateOf(setOf<Char>())
        private set


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
}