# Hangman
 
An Android Hangman game built with Jetpack Compose.
 

## Gameplay
 
- A random word is picked from a bundled word list, filtered by difficulty.
- Guess letters using the on-screen keyboard: tap a letter to select it, then **Enter** to confirm the guess (or **⌫** to change your mind before confirming).
- Each wrong guess costs a life; the hangman drawing builds up one piece at a time as lives are lost.
- Win by revealing every letter in the word before running out of lives.
- One hint is available per word — it reveals a random unguessed letter, at the cost of a life. Hints prefer letters that only appear once in the word, so a single hint doesn't accidentally reveal several tiles at once.


## Features
 
- **Difficulty levels** — Easy, Medium, and Hard filter the word pool by word length. Changing difficulty starts a new word immediately.
- **On-screen keyboard** — tappable A–Z keys instead of a text field. Keys are colour-coded: green for correct, grey for incorrect, yellow for the currently selected (not-yet-confirmed) letter, and disabled once a letter has been guessed, so the same letter can't be guessed twice.
- **Hint system** — one hint per word, costs one life.
- **Progressive hangman drawing** — drawn on a `Canvas`, one body part added per wrong guess.
- **Restart** — play again with a new word once a round ends.


## Architecture
 
- **`HangmanViewModel`** (`AndroidViewModel`) — owns all game state: the word list, current word, difficulty, guessed letters, and lives used. Survives configuration changes (e.g. screen rotation). Exposes plain functions (`guessLetter`, `useHint`, `changeDifficulty`, `restart`, etc.) rather than letting the UI mutate state directly — state that shouldn't be changed from outside the ViewModel is marked `private set`.
- **`HangmanScreen`** — the single Compose screen. Holds only UI-local state (`pendingLetter`, the letter selected but not yet confirmed); everything about the actual game lives in the ViewModel.
- **`OnScreenKeyboard`**, **`DifficultySelector`**, **`HangmanDrawing`** — smaller composables the screen is built from.


## Word list
 
Words are bundled at `app/src/main/res/raw/words.txt` and loaded once when the ViewModel is created, via `Resources.openRawResource`. Loaded words are lowercased and trimmed; words under 3 letters are filtered out at load time.
 
Difficulty buckets (by word length):
- Easy: 3–4 letters
- Medium: 5–7 letters
- Hard: 8+ letters
If a difficulty's bucket happens to be empty, the game falls back to the full word list rather than crashing.
 

## Edge cases handled
 
- The same word won't be picked twice in a row after a restart.
- A difficulty bucket with no matching words falls back to the full list.
- Backspacing to an empty text/selection state doesn't crash or misbehave.
- Hints avoid revealing more than one tile where possible, by preferring letters that appear only once in the target word.


## Tech stack
 
- Kotlin
- Jetpack Compose (Material 3)
- `AndroidViewModel` / `mutableStateOf` for state management
- Compose `Canvas` for the hangman drawing

  
## Running the project
 
Open in Android Studio and run the `app` configuration on an emulator or device (`minSdk`/`targetSdk` as configured in `app/build.gradle.kts`).


## Possible next steps
 
- Persist best streak / stats across app launches (`SharedPreferences` or `DataStore`).
- Split the word list into categories the player can choose from.
- Unit tests for `HangmanViewModel` (`guessLetter`, `isWon`, `isLost`, `pickWord`), which are pure logic and don't need Compose to test.
