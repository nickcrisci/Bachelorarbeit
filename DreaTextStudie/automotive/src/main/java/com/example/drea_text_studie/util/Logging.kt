package com.example.drea_text_studie.util

import android.util.Log
import android.widget.Button
import org.json.JSONObject

const val TAG = "Study"

enum class Events {
    Char_Clicked,
    Char_Selected,
    Word_Next,
    Word_Done,
}

fun charClicked(char: Button) {
    val json = "{\"event\": \"${Events.Char_Clicked}\",\"character\": \"${char.text}\"}"
    Log.i(TAG, json)
}

fun selectedChar(char: Button) {
    val json = "{\"event\": \"${Events.Char_Selected}\",\"character\": \"${char.text}\"}"
    Log.i(TAG, json)
}

fun nextWord(word: String) {
    val json = "{\"event\": \"${Events.Word_Next}\",\"word\": \"$word\"}"
    Log.i(TAG, json)
}

fun wordDone(targetWord: String, received: String) {
    val json = "{\"event\": \"${Events.Word_Done}\",\"target\": \"$targetWord\", \"received\": \"$received\"}"
    Log.i(TAG, json)
}