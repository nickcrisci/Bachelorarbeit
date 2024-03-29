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

// Data String im CSV Format:
// event, timestamp, button, finger, word

private fun getTimestamp(): Long {
    return System.currentTimeMillis()
}

fun trialStarted() {
    val csv = "{${Events.TRIAL_START},${getTimestamp()},None,None,None}"
    Log.i(TAG, csv)
}

fun trialEnded() {
    val csv = "{${Events.TRIAL_END},${getTimestamp()},None,None,None}"
    Log.i(TAG, csv)
}

fun charClicked(char: Button) {
    val csv = "{${Events.CHAR_CLICKED},${getTimestamp()},${char.text},None,None}"
    Log.i(TAG, csv)
}

fun selectedChar(char: Button, finger: Int) {
    val csv = "{${Events.CHAR_SELECTED},${getTimestamp()},${char.text},$finger,None}"
    Log.i(TAG, csv)
}

fun nextWord(word: String) {
    val csv = "{${Events.WORD_NEXT},${getTimestamp()},None,None,$word}"
    Log.i(TAG, csv)
}

fun wordDone(word: String) {
    val csv = "{${Events.WORD_DONE},${getTimestamp()},None,None,$word}"
    Log.i(TAG, csv)
}

/*fun trialStarted() {
    val json = "{\"event\": \"${Events.TRIAL_START}\", \"timestamp\": ${getTimestamp()}}"
    Log.i(TAG, json)
}

fun trialEnded() {
    val json = "{\"event\": \"${Events.TRIAL_END}\", \"timestamp\": ${getTimestamp()}}"
    Log.i(TAG, json)
}

fun charClicked(char: Button) {
    val json = "{\"event\": \"${Events.CHAR_CLICKED}\",\"character\": \"${char.text}\", \"timestamp\": ${getTimestamp()}}"
    Log.i(TAG, json)
}

fun selectedChar(char: Button) {
    val json = "{\"event\": \"${Events.CHAR_SELECTED}\",\"character\": \"${char.text}\", \"timestamp\": ${getTimestamp()}}"
    Log.i(TAG, json)
}

fun nextWord(word: String) {
    val json = "{\"event\": \"${Events.WORD_NEXT}\",\"word\": \"$word\", \"timestamp\": ${getTimestamp()}}"
    Log.i(TAG, json)
}

fun wordDone(targetWord: String, received: String) {
    val json = "{\"event\": \"${Events.WORD_DONE}\",\"target\": \"$targetWord\", \"received\": \"$received\", \"timestamp\": ${getTimestamp()}}"
    Log.i(TAG, json)
}*/