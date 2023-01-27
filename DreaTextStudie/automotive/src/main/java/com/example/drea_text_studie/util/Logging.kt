package com.example.drea_text_studie.util

import android.util.Log
import android.widget.Button

const val TAG = "Study"

enum class Events {
    CHAR_CLICKED,
    CHAR_SELECTED,
    WORD_NEXT,
    WORD_DONE,
    TRIAL_START,
    TRIAL_END
}

private fun getTimestamp(): Long {
    return System.currentTimeMillis()
}

fun trialStarted() {
    val json = "{\"event\": \"${Events.TRIAL_START}\", \"timestamp\": \"${getTimestamp()}\"}"
    Log.i(TAG, json)
}

fun trialEnded() {
    val json = "{\"event\": \"${Events.TRIAL_END}\", \"timestamp\": \"${getTimestamp()}\"}"
    Log.i(TAG, json)
}

fun charClicked(char: Button) {
    val json = "{\"event\": \"${Events.CHAR_CLICKED}\",\"character\": \"${char.text}\", \"timestamp\": \"${getTimestamp()}\"}"
    Log.i(TAG, json)
}

fun selectedChar(char: Button) {
    val json = "{\"event\": \"${Events.CHAR_SELECTED}\",\"character\": \"${char.text}\", \"timestamp\": \"${getTimestamp()}\"}"
    Log.i(TAG, json)
}

fun nextWord(word: String) {
    val json = "{\"event\": \"${Events.WORD_NEXT}\",\"word\": \"$word\", \"timestamp\": \"${getTimestamp()}\"}"
    Log.i(TAG, json)
}

fun wordDone(targetWord: String, received: String) {
    val json = "{\"event\": \"${Events.WORD_DONE}\",\"target\": \"$targetWord\", \"received\": \"$received\", \"timestamp\": \"${getTimestamp()}\"}"
    Log.i(TAG, json)
}