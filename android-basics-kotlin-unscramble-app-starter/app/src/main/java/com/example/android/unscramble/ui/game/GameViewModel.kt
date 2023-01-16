package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.random.Random

class SaveableMutableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    initialValue: T
) {
    private val state: StateFlow<T> = savedStateHandle.getStateFlow(key, initialValue)
    var value: T
        get() = state.value
        set(value) {
            savedStateHandle[key] = value
        }

    fun asStateFlow(): StateFlow<T> = state
}

fun <T> SavedStateHandle.getMutableStateFLow(
    key: String,
    initialValue: T
): SaveableMutableStateFlow<T> =
    SaveableMutableStateFlow(this, key, initialValue)


class GameViewModel(private val stateHandler: SavedStateHandle) : ViewModel() {

    private val _score = stateHandler.getMutableStateFLow("score", 0)
    val score: StateFlow<Int>
        get() = _score.asStateFlow()

//    val score: StateFlow<Int> = stateHandler.getStateFlow("score", 0)
//    private fun setScore(value: Int) {
//        stateHandler["score"] = value
//    }

    private val _currentWordCount = stateHandler.getMutableStateFLow("currentWordCount", 0)
    val currentWordCount: StateFlow<Int>
        get() = _currentWordCount.asStateFlow()

    private val _currentScrambledWord = stateHandler.getMutableStateFLow("currentScrambledWord", "")
    val currentScrambledWord: StateFlow<Spannable> = _currentScrambledWord
        .asStateFlow()
        .onSubscription {
            // TODO nextWord() if necessary
            if (currentWord.isEmpty())
                nextWord()
        }
        .map { scrambledWord ->
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SpannableString(""))

    // List of words used in the game
    private var wordsList: List<String>
        get() = stateHandler["wordsList"] ?: emptyList()
        set(value) {
            stateHandler["wordsList"] = value
        }

    private var currentWord: String
        get() = stateHandler["currentWord"] ?: ""
        set(value) {
            val tempWord = currentWord.toCharArray()
            do {
                tempWord.shuffle()
            } while (String(tempWord) == value)
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value += 1
            wordsList = wordsList + currentWord
            stateHandler["currentWord"] = value
        }

//    private fun getNextWord() {
//        var nextWord: String
//        do {
//            nextWord = allWordsList.random(Random(Calendar.getInstance().timeInMillis))
//        } while (wordsList.contains(currentWord))
//        currentWord = nextWord
//    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList = emptyList()
        nextWord()
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value < MAX_NO_OF_WORDS) {
            var nextWord: String
            do {
                nextWord = allWordsList.random(Random(Calendar.getInstance().timeInMillis))
            } while (wordsList.contains(currentWord))
            currentWord = nextWord
            true
        } else false
    }

    private fun increaseScore() {
        _score.value += SCORE_INCREASE
    }

    fun isUserWordCorrcet(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }
}

