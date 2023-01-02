package com.example.drea_text_studie.util

import kotlin.math.abs

class Accumulator(private val rotationThreshold: Double) {
    private var posCounter = 0
    private var negCounter = 0

    private val counterThreshold = 4
    private var lastMsgReceived: Long = 0

    private var lastPosMsg: Long = 0
    private var lastNegMsg: Long = 0
    private val timeout = 100

    private var _negativeAcc: Double = 0.0
    val negativeAcc: Double
        get() = _negativeAcc

    private var _positiveAcc: Double = 0.0
    val positiveAcc: Double
        get() = _positiveAcc

    fun accumulate(ts: Long, rotation: Double) {
        if ((ts - lastMsgReceived) > 0.25 * 1000) {
            _positiveAcc = 0.0
            _negativeAcc = 0.0
        }
        lastMsgReceived = ts

        if (rotation > 0) {
            _positiveAcc += rotation
        } else {
            _negativeAcc += rotation
        }
    }

    fun checkForGesture(): Direction? {
        var direction: Direction? = null
        if (_positiveAcc > rotationThreshold) {
            direction = Direction.RIGHT
            _positiveAcc -= rotationThreshold
        } else if (abs(_negativeAcc) > rotationThreshold) {
            direction = Direction.LEFT
            _negativeAcc += rotationThreshold
        }
        return direction
    }

    fun reset(ts: Long, rotation: Double, resetFunction: (Double, Long) -> Unit) {
        resetFunction(rotation, ts)
    }

    val resetWhenCounter: (Double, Long) -> Unit = { rotation, _ ->
        if (rotation >= 0) {
            posCounter++
            negCounter = 0
            if (posCounter > counterThreshold) {
                _negativeAcc = 0.0
                posCounter = 0
            }
        } else {
            negCounter++
            posCounter = 0
            if (negCounter > counterThreshold) {
                _positiveAcc = 0.0
                negCounter = 0
            }
        }
    }
    val resetWhenTimeout: (Double, Long) -> Unit = { rotation, ts ->
        if (rotation >= 0) {
            lastPosMsg = ts
            // Bei positiver Rotation wird der Zeitstempel mit dem Zeitstempel
            // der letzten negativen Rotation verglichen
            if ((ts - lastNegMsg) > timeout) {
                _negativeAcc = 0.0
            }
        } else {
            lastNegMsg = ts
            // Bei negativer Rotation wird der Zeitstempel mit dem Zeitstempel
            // der letzten positiven Rotation verglichen
            if ((ts - lastPosMsg) > timeout) {
                _positiveAcc = 0.0
            }
        }
    }
    val resetWhenSignChanges: (Double, Long) -> Unit = { rotation, _ ->
        if (rotation >= 0) {
            _negativeAcc = 0.0
        } else {
            _positiveAcc = 0.0
        }
    }
}