package com.example.drea_text_studie.ui.studie

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.drea_text_studie.R
import com.example.drea_text_studie.databinding.CharButtonBinding
import com.example.drea_text_studie.databinding.FragmentWordsBinding
import com.example.drea_text_studie.util.Direction
import com.example.drea_text_studie.util.charClicked
import com.example.drea_text_studie.util.selectedChar
import com.example.drea_text_studie.util.wordDone
import org.eclipse.paho.client.mqttv3.*
import java.util.concurrent.Executor
import kotlin.math.abs
import com.example.drea_text_studie.mqtt.DreaMqttClient

val CHARS = listOf(
    (1..9).plus(0),
    ('A'..'J').toList(),
    ('K'..'T').toList(),
    ('U'..'Z').toList()
)

class WordsFragment : Fragment() {

    private val STUDY_TAG = "Study"
    private val args: WordsFragmentArgs by navArgs()

    private val threshold = 36.0
    private var lastMsgReceived: Long = 0
    private var lastRotation: Double = 0.0
    private var rotationAcc: Double = 0.0

    private lateinit var charButtonBinding: CharButtonBinding

    private lateinit var binding: FragmentWordsBinding
    private val viewModel: WordsViewModel by viewModels()

    private lateinit var SELECTED_DRAWABLE: Drawable
    private lateinit var selected: Button

    private val executor = ThreadPerTaskExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_words, container, false)

        SELECTED_DRAWABLE = AppCompatResources.getDrawable(requireContext(), R.drawable.selected)!!
        for (chunk in CHARS) {
            val row = TableRow(context)
            for (char in chunk) {
                charButtonBinding =
                    DataBindingUtil.inflate(inflater, R.layout.char_button, null, false)
                with(charButtonBinding.root as Button) {
                    text = char.toString()
                    id = "${binding.charTable.childCount}${row.childCount}".toInt()
                    setOnClickListener {
                        binding.textInput.append(text)
                        charClicked(this)
                    }
                }
                row.addView(charButtonBinding.root)
            }
            binding.charTable.addView(row)
        }

        selected = (binding.charTable[0] as TableRow)[0] as Button

        var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
        selBinding!!.sel = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            wordsViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            nextWord.setOnClickListener {
                viewModel.getNextWord()
            }
            btnPrevious.setOnClickListener {
                selectedChar(selectNextChar(Direction.LEFT))
            }
            btnNext.setOnClickListener {
                selectedChar(selectNextChar(Direction.RIGHT))
            }
            btnDone.setOnClickListener {
                wordDone(currentWord.text.toString(), textInput.text.toString())
                textInput.text = ""
                val done = viewModel.getNextWord()
                if (done) {
                    textInput.text = "Du bist fertig!"
                    Log.i(STUDY_TAG, "Trial is finished")
                    btnDone.isClickable = false
                    btnNext.isClickable = false
                }
            }
        }
        viewModel.getNextWord()
        val isActive = false
        val mqttClient = DreaMqttClient("tcp://10.0.2.2:1883", MqttClient.generateClientId(), context)
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.keepAliveInterval = 1200
        val token = mqttClient.connect(options)

        mqttClient.client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d(STUDY_TAG, "Connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                executor.execute(messageRunnable(message))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                TODO("Not yet implemented")
            }
        })
    }

    inner class bindingRunnable(val direction: Direction): Runnable {
        override fun run() {
            var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
            selBinding!!.sel = false
            val indices = viewModel.selectNext(direction)

            selected = (binding.charTable[indices[0]] as TableRow)[indices[1]] as Button
            selectedChar(selected)
            selBinding = DataBindingUtil.findBinding(selected)
            selBinding!!.sel = true
        }
    }

    inner class messageRunnable(val message: MqttMessage?): Runnable {
        override fun run() {
            try {
                val msg = message.toString().split(",")
                if (msg.size != 4) return

                val fingerCount = msg[1].toInt()
                if (fingerCount < 2) return

                val ts = msg[0].toLong(10);
                // Wenn länger als 0.5 Sekunden kein Finger am Controller war wird Accumulator zurückgesetzt
                if ((ts - lastMsgReceived) > 0.5 * 1000) rotationAcc = 0.0

                lastMsgReceived = ts

                val rotation = msg[2].substring(0, 4).toDouble()

                rotationAcc += rotation
                //Log.d(STUDY_TAG, rotationAcc.toString())
                if (abs(rotationAcc) >= threshold) {
                    val direction = if (rotationAcc > 0) Direction.RIGHT else Direction.LEFT
                    rotationAcc = 0.0
                    executor.execute(bindingRunnable(direction))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun selectNextChar(direction: Direction): Button {
        var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
        selBinding!!.sel = false
        val indices = viewModel.selectNext(direction)

        selected = (binding.charTable[indices[0]] as TableRow)[indices[1]] as Button
        selectedChar(selected)
        selBinding = DataBindingUtil.findBinding(selected)
        selBinding!!.sel = true
        return selected
    }
}

class ThreadPerTaskExecutor: Executor {
    override fun execute(p0: Runnable?) {
        Thread(p0).start()
    }
}