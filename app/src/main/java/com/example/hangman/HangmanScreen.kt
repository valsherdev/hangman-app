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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement

private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"
@Composable
fun HangmanScreen(viewModel: HangmanViewModel = viewModel()) {
    var pendingLetter by remember { mutableStateOf<Char?>(null) }
    var showHintInfo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            color = Color(0xFFEAF2F8),
            shape = RoundedCornerShape(16.dp)
        ) {
                Text(
                    text = "HANGMAN",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 5.sp,
                    color = Color(0xFF243B64),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                )
            }
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Pick your level of difficulty:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF444444),
            modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)
        )

        DifficultySelector(
            currentDifficulty = viewModel.difficulty,
            onSelect = { viewModel.pickDifficulty(it) }
        )

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEAF2F8)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                HangmanDrawing(
                    livesRemaining = viewModel.getLivesRemaining(),
                    maxLives = viewModel.maxLives,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )

                Text(
                    text = viewModel.getDisplayWord(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                )

                Text(
                    text = "❤️".repeat(viewModel.getLivesRemaining()) +
                            "🖤".repeat(viewModel.maxLives - viewModel.getLivesRemaining()),
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }
        }

        if (showHintInfo) {
            AlertDialog(
                onDismissRequest = {
                    showHintInfo = false
                },
                title = {
                    Text("How hints work")
                },
                text = {
                    Text("A hint reveals one random letter from the word. " +
                            "Using a hint costs one heart, so you need at least two hearts to use it. " +
                            "You only have 1 hint.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showHintInfo = false
                        }
                    ) {
                        Text("Got it")
                    }
                }
            )
        }

        if (viewModel.isGameOver()) {
            Text(
                text = if (viewModel.isWon()) {
                    "🎉 YOU WON! 🎉" }
                else {
                    "💀 GAME OVER 💀" },
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF222222),
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            )

            if (!viewModel.isWon()) {
                Text(
                    text = "The word was: ${viewModel.targetWord}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF555555),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )}

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        viewModel.restart()
                        pendingLetter = null
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Play again")
                }
            }


        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.useHint()
                    },
                    enabled = !viewModel.hintUsed &&
                            viewModel.getLivesRemaining() > 1,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("💡 Hint")
                }

                Button(
                    onClick = {
                        showHintInfo = true
                    },
                    modifier = Modifier.padding(start = 8.dp).width(50.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("ⓘ",
                        fontSize = 22.sp)
                }
            }

            OnScreenKeyboard(
                viewModel = viewModel,
                pendingLetter = pendingLetter,
                onLetterTap = { pendingLetter = it },
                onBackspace = { pendingLetter = null },
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
fun DifficultySelector(currentDifficulty: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp), horizontalArrangement = Arrangement.Center) {
        DifficultyButton("easy", "Easy", currentDifficulty, onSelect)
        DifficultyButton("medium", "Medium", currentDifficulty, onSelect)
        DifficultyButton("hard", "Hard", currentDifficulty, onSelect)
    }
}

@Composable
fun DifficultyButton(
    value: String,
    label: String,
    currentDifficulty: String,
    onSelect: (String) -> Unit
) {
    val isSelected = value == currentDifficulty
    Button(
        onClick = { onSelect(value) },
        modifier = Modifier.padding(2.dp),
        colors = if (isSelected) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        }
    ) {
        Text(label)
    }
}

@Composable
fun HangmanDrawing(livesRemaining: Int, maxLives: Int, modifier: Modifier = Modifier)
{
    val mistakes = maxLives - livesRemaining
    val hangmanColor = MaterialTheme.colorScheme.onBackground

    Canvas(
        modifier = modifier)
    {
        val strokeWidth = 12f

        val manX = size.width * 0.7f

        drawLine(
            color = hangmanColor,
            start = Offset(size.width * 0.15f, size.height - 25f),
            end = Offset(size.width * 0.85f, size.height - 25f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = hangmanColor,
            start = Offset(size.width * 0.30f, size.height - 25f),
            end = Offset(size.width * 0.30f, 30f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = hangmanColor,
            start = Offset(size.width * 0.30f, 30f),
            end = Offset(manX, 30f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = hangmanColor,
            start = Offset(manX, 30f),
            end = Offset(manX, 180f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        if (mistakes >= 1) {
            drawCircle(
                color = hangmanColor,
                center = Offset(manX, 230f),
                radius = 50f,
                style = Stroke(width = strokeWidth)
            )
        }
        if (mistakes >= 2) {
            drawLine(
                color = hangmanColor,
                start = Offset(manX, 280f),
                end = Offset(manX, 375f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        if (mistakes >= 3) {
            drawLine(
                color = hangmanColor,
                start = Offset(manX, 300f),
                end = Offset(manX - 80f, 355f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        if (mistakes >= 4) {
            drawLine(
                color = hangmanColor,
                start = Offset(manX, 300f),
                end = Offset(manX + 80f, 355f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        if (mistakes >= 5) {
            drawLine(
                color = hangmanColor,
                start = Offset(manX, 375f),
                end = Offset(manX - 70f, 455f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        if (mistakes >= 6) {
            drawLine(
                color = hangmanColor,
                start = Offset(manX, 375f),
                end = Offset(manX + 70f, 455f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}