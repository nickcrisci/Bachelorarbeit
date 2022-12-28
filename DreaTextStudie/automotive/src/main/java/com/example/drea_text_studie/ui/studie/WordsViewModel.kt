package com.example.drea_text_studie.ui.studie

import com.example.drea_text_studie.util.Direction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drea_text_studie.util.nextWord

class WordsViewModel : ViewModel() {

    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    // Variables important for keyboard
    private val maxRowLength = 10
    private val maxRows = 4
    private val lastRowLength = 6
    private var rowCounter = 0
    private var itemCounter = 0

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

    fun selectNext(direction: Direction): List<Int> {
        return if (direction == Direction.LEFT) {
            selectPrevious()
        } else {
            selectNext()
        }
    }

    private fun selectNext(): List<Int> {
        if (rowCounter == maxRows - 1) {
            if (itemCounter == lastRowLength - 1) {
                rowCounter = 0
                itemCounter = 0
            } else {
                itemCounter++
            }
        } else {
            if (itemCounter == maxRowLength - 1) {
                rowCounter++
                itemCounter = 0
            } else {
                itemCounter++
            }
        }
        return listOf(rowCounter, itemCounter)
    }

    private fun selectPrevious(): List<Int> {
        if (itemCounter == 0) {
            if (rowCounter == 0) {
                rowCounter = maxRows - 1
                itemCounter = lastRowLength - 1
            } else {
                rowCounter--
                itemCounter = maxRowLength - 1
            }
        } else {
            itemCounter--
        }
        return listOf(rowCounter, itemCounter)
    }
}