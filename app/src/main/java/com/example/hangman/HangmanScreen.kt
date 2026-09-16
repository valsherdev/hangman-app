package com.example.hangman

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HangmanScreen(viewModel: HangmanViewModel = viewModel()) {
    var letterInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(40.dp)) {
        Text(
            text = viewModel.getDisplayWord()
        )

        Text(
            text = "❤️".repeat(viewModel.getLivesRemaining()) +
                    "🖤".repeat(viewModel.maxLives - viewModel.getLivesRemaining()),
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Your guessed letters: ${viewModel.guessedLetters.sorted().joinToString(", ")}",
            modifier = Modifier.padding(top = 8.dp)
        )

        if (viewModel.isGameOver()) {
            Text(
                text = if (viewModel.isWon()) "You won!" else "You lost! The word was: ${viewModel.targetWord}",
                modifier = Modifier.padding(top = 24.dp)
            )

            Button(
                onClick = {
                    viewModel.restart()
                    letterInput = ""
                          },
                modifier = Modifier.padding(top = 16.dp)
            ){
                Text("Play again")
            }

        } else {
            TextField(
                value = letterInput,
                onValueChange = {newValue ->
                    if (newValue.length <= 1) {
                        if (newValue.isEmpty() || newValue[0].isLetter()) {
                            letterInput = newValue
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.padding(top = 24.dp)
            )
            Button(
                onClick = {
                    val letter = letterInput.firstOrNull()
                    if (letter != null) {
                        viewModel.guessLetter(letter)
                    }
                    letterInput = ""
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Submit")
            }
        }
    }
}