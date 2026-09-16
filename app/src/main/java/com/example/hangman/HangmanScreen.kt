package com.example.hangman

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
    }
}