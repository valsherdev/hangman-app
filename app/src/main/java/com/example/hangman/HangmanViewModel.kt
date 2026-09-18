package com.example.hangman

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel


class HangmanViewModel(application: Application) : AndroidViewModel(application) {


    private val words: List<String> = loadWords().filter { it.length >= 3 }

    var difficulty by mutableStateOf("medium")
        private set
    var targetWord by mutableStateOf(pickWord(difficulty))

    val maxLives = 6
    var usedLives by mutableStateOf(0)
        private set

    var hintUsed by mutableStateOf(false)
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


    private fun pickWord(difficulty: String, excludeWord: String? = null): String {
        val lengthFiltered = when (difficulty) {
            "easy" -> words.filter { it.length in 3..4 }
            "hard" -> words.filter { it.length >= 8 }
            else -> words.filter { it.length in 5..7 }
        }.ifEmpty { words }

        val candidates = lengthFiltered.filter { it != excludeWord }.ifEmpty { lengthFiltered }
        return candidates.random()
    }


    fun pickDifficulty(newDifficulty: String) {
        difficulty = newDifficulty
        restart()

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

    fun useHint() {
        if (isGameOver()) return
        if (usedLives >= maxLives) return
        if (hintUsed || getLivesRemaining() <= 1) return

        val availableLetters = targetWord
            .toSet()
            .filter { it !in guessedLetters }

        if (availableLetters.isEmpty()) return

        val singleOccurrenceLetters = availableLetters.filter { letter ->
            targetWord.count { it == letter } == 1
        }
        val hintPool = singleOccurrenceLetters.ifEmpty { availableLetters }

        val hintLetter = hintPool.random()

        guessedLetters = guessedLetters + hintLetter
        usedLives++
        hintUsed = true
    }

    fun restart() {
        targetWord = pickWord(difficulty, excludeWord = targetWord)
        guessedLetters = setOf<Char>()
        usedLives = 0
        hintUsed = false
    }


    fun letterStatus(letter: Char): String {
        if (letter !in guessedLetters) return "unguessed"
        return if (letter in targetWord) "correct" else "wrong"
    }
}