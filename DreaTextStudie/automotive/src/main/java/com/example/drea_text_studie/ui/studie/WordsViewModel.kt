package com.example.drea_text_studie.ui.studie

import android.util.Log
import android.view.View
import com.example.drea_text_studie.util.Direction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drea_text_studie.util.nextWord

class WordsViewModel : ViewModel() {

    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    var mistakeCounter = 0

    var mode = 1

    // Variables important for keyboard
    private val maxRowLength = 10
    private val maxRows = 4
    private val lastRowLength = 8
    private var rowCounter = 0
    private var itemCounter = 0

    private var wordsList: MutableList<String> = mutableListOf()

    /**
     * Funktion die überprüft, ob der eingegebene Buchstabe richtig war
     *
     * @param inputWord das bisher eingegebene Wort
     * @param input der geklickte Buchstabe
     * @return ob die Eingabe korrekt war
     */
    fun spellCheck(inputWord: String, input: String): Boolean {
        val word = currentWord.value!!.toList()
        val currentChar = word[inputWord.length].uppercase()
        if(currentChar == input) {
            return true
        }
        mistakeCounter++
        return false
    }

    fun wordCheck(inputWord: String): Boolean {
        return inputWord == currentWord.value!!.uppercase()
    }

    /**
     * Funktion die das nächste einzugebende Wort liefert
     *
     * @return Ob alle Worte eingegeben wurden. Ist die Liste leer, so wird true zurückgegeben
     */
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

    fun selectNext(direction: Direction, finger: Int): List<Int> {
        // drea mode
        when (mode) {
            0 -> return if (direction == Direction.LEFT) {
                selectPrevious()
            } else {
                selectNext()
            }
            else -> return if (direction == Direction.LEFT) {
                dreaSelectPrev(finger)
            } else {
                dreaSelectNext(finger)
            }
        }
    }

    /**
     * Berechnet den Index für den nächsten Buchstaben im normalen Modus
     * @return den Index als list<Int>: Reihenindex und Positionsindex
     */
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

    /**
     * Berechnet den Index für den vorherigen Buchstaben im normalen Modus
     * @return den Index als list<Int>: Reihenindex und Positionsindex
     */
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

    /**
     * Funktion um einen Reihensprung durchzuführen
     *
     * Diese Funktionen ermöglicht den Sprung auf den gleichen Index in der gewollten Reihe.
     * Dabei muss beachtet werden, dass die Liste der letzten Reihe weniger Elemente enthält,
     * da "SPACE" und "ENTER" jeweils zwei Plätze beeinspruchen.
     * Daher muss der Index bei einem Sprung in die letzte Reihe ggf. angepasst werden.
     */
    private fun rowJump(rowIndex: Int) {
        if (rowIndex == maxRows - 1) {
            itemCounter = when(itemCounter) {
                7 -> 6
                8, 9 -> 7
                else -> itemCounter
            }
        } else if (rowCounter == maxRows - 1 && itemCounter == 7) {
            itemCounter = 8
        }
    }

    /**
     * Berechnet den Index für den nächsten Buchstaben im DREA Modus
     * @param finger die Anzahl an Finger am Controller
     * @return den Index als list<Int>: Reihenindex und Positionsindex
     */
    private fun dreaSelectNext(finger: Int): List<Int> {
        val rowIndex = when(finger) {
            2 -> 0
            3 -> 1
            4 -> 2
            else -> 3
        }

        // Sprung auf gleichen Index der nächsten Zeile
        if (rowCounter != rowIndex) {
            rowJump(rowIndex)
        } else {
            if (rowIndex == 3) {
                if (itemCounter == lastRowLength - 1) {
                    itemCounter = 0
                } else {
                    itemCounter++
                }
            } else { // Wenn nicht letzte Reihe
                // Wenn letztes Item in Reihe
                if (itemCounter == maxRowLength - 1) {
                    itemCounter = 0
                } else { // Wenn NICHT letztes Item in Reihe
                    itemCounter++
                }
            }
        }

        rowCounter = rowIndex

        return listOf(rowCounter, itemCounter)
    }

    /**
     * Berechnet den Index für den vorherigen Buchstaben im DREA Modus
     * @param finger die Anzahl an Finger am Controller
     * @return den Index als list<Int>: Reihenindex und Positionsindex
     */
    private fun dreaSelectPrev(finger: Int): List<Int> {
        val rowIndex = when(finger) {
            2 -> 0
            3 -> 1
            4 -> 2
            else -> 3
        }

        if (rowCounter != rowIndex) {
            rowJump(rowIndex)
        } else {
            if (rowIndex == 3) {
                if (itemCounter == 0) {
                    itemCounter = lastRowLength - 1
                } else {
                    itemCounter--
                }
            } else { // Wenn nicht letzte Reihe
                // Wenn letztes Item in Reihe
                if (itemCounter == 0) {
                    itemCounter = maxRowLength - 1
                } else { // Wenn NICHT letztes Item in Reihe
                    itemCounter--
                }
            }
        }

        rowCounter = rowIndex

        return listOf(rowCounter, itemCounter)
    }
}