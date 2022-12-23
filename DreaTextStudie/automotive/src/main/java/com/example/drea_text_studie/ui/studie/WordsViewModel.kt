package com.example.drea_text_studie.ui.studie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drea_text_studie.util.nextWord

class WordsViewModel : ViewModel() {

    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    private var wordsList: MutableList<String> = mutableListOf()

    fun getNextWord(): Boolean {
        val word = allWordsList.random()
        if (wordsList.contains(word)) {
            if (wordsList.size == allWordsList.size) {
                return true
            }
            getNextWord()
        } else {
            _currentWord.value = word
            nextWord(word)
            wordsList.add(word)
        }
        return false
    }
}