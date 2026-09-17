package com.example.hangman

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color


private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"
@Composable
fun HangmanScreen(viewModel: HangmanViewModel = viewModel()) {
    var pendingLetter by remember { mutableStateOf<Char?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(40.dp)) {

        Text(
            text = "HANGMAN",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        HangmanDrawing(
            livesRemaining = viewModel.getLivesRemaining(),
            maxLives = viewModel.maxLives
        )

        Text(
            text = viewModel.getDisplayWord()
        )

        Text(
            text = "❤️".repeat(viewModel.getLivesRemaining()) +
                    "🖤".repeat(viewModel.maxLives - viewModel.getLivesRemaining()),
            modifier = Modifier.padding(top = 16.dp)
        )


        if (viewModel.isGameOver()) {
            Text(
                text = if (viewModel.isWon()) "You won!" else "You lost! The word was: ${viewModel.targetWord}",
                modifier = Modifier.padding(top = 24.dp)
            )

            Button(
                onClick = {
                    viewModel.restart()
                    pendingLetter = null
                          },
                modifier = Modifier.padding(top = 16.dp)
            ){
                Text("Play again")
            }

        } else
            OnScreenKeyboard(
                viewModel = viewModel,
                pendingLetter = pendingLetter,
                onLetterTap = { pendingLetter = it },
                onBackspace = { pendingLetter = null},
                onEnter = {
                    val letter = pendingLetter
                    if (letter != null) {
                        viewModel.guessLetter(letter)
                    }
                    pendingLetter = null
                }
            )

        }
    }



@Composable
fun OnScreenKeyboard(
    viewModel: HangmanViewModel,
    pendingLetter: Char?,
    onLetterTap: (Char) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        for (row in ALPHABET.chunked(9)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (letter in row) {
                    val status = viewModel.letterStatus(letter)
                    val isSelected = letter == pendingLetter

                    val colors = when {
                        status == "correct" -> ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6AAA64),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF6AAA64),
                            disabledContentColor = Color.White
                        )
                        status == "wrong" -> ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF787C7E),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF787C7E),
                            disabledContentColor = Color.White
                        )
                        isSelected -> ButtonDefaults.buttonColors(containerColor = Color(0xFFC9B458))
                        else -> ButtonDefaults.buttonColors()
                    
                    }
                    Button(
                        onClick = { onLetterTap(letter) },
                        enabled = status == "unguessed",
                        colors = colors,
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier.weight(1f).padding(1.dp)
                    ) {
                        Text(letter.uppercase())
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(
                onClick = onBackspace,
                enabled = pendingLetter != null,
                modifier = Modifier.weight(1f).padding(1.dp)
            ) {
                Text("⌫")
            }
            Button(
                onClick = onEnter,
                enabled = pendingLetter != null,
                modifier = Modifier.weight(1f).padding(1.dp)
            ) {
                Text("Enter")
            }
        }
    }
}

@Composable
fun HangmanDrawing(
    livesRemaining: Int,
    maxLives: Int
) {
    val mistakes = maxLives - livesRemaining
    val hangmanColor = MaterialTheme.colorScheme.onBackground

    Canvas(
        modifier = Modifier.fillMaxWidth().height(250.dp)
    ) {

        val strokeWidth = 8f

        drawLine(
            color = hangmanColor,
            start = Offset(40f, size.height - 20f),
            end = Offset(size.width - 40f, size.height - 20f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = hangmanColor,
            start = Offset(100f, size.height - 20f),
            end = Offset(100f, 30f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = hangmanColor,
            start = Offset(100f, 30f),
            end = Offset(size.width * 0.65f, 30f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = hangmanColor,
            start = Offset(size.width * 0.65f, 30f),
            end = Offset(size.width * 0.65f, 75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        if (mistakes >= 1) {
            drawCircle(
                color = hangmanColor,
                center = Offset(size.width * 0.65f, 105f),
                radius = 25f,
                style = Stroke(width = strokeWidth)
            )
        }

        if (mistakes >= 2) {
            drawLine(
                color = hangmanColor,
                start = Offset(size.width * 0.65f, 130f),
                end = Offset(size.width * 0.65f, 205f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        if (mistakes >= 3) {
            drawLine(
                color = hangmanColor,
                start = Offset(size.width * 0.65f, 155f),
                end = Offset(size.width * 0.57f, 175f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        if (mistakes >= 4) {
            drawLine(
                color = hangmanColor,
                start = Offset(size.width * 0.65f, 155f),
                end = Offset(size.width * 0.73f, 175f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        if (mistakes >= 5) {
            drawLine(
                color = hangmanColor,
                start = Offset(size.width * 0.65f, 205f),
                end = Offset(size.width * 0.60f, 260f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        if (mistakes >= 6) {
            drawLine(
                color = hangmanColor,
                start = Offset(size.width * 0.65f, 205f),
                end = Offset(size.width * 0.70f, 260f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}