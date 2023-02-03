package com.example.drea_text_studie.ui.studie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.example.drea_text_studie.R
import com.example.drea_text_studie.databinding.CharButtonBinding
import com.example.drea_text_studie.databinding.FragmentWordsBinding
import com.example.drea_text_studie.mqtt.DreaMqttClient
import com.example.drea_text_studie.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import java.util.concurrent.Executor

val CHARS = listOf(
    (1..9).plus(0),
    ('A'..'J').toList(),
    ('K'..'T').toList(),
    ('U'..'Z').toList().plus(listOf("SPACE", "ENTER"))
)

class WordsFragment : Fragment() {

    private val STUDY_TAG = "Study"
    private val args: WordsFragmentArgs by navArgs()

    private var finger: Int = 2

    private lateinit var charButtonBinding: CharButtonBinding

    private lateinit var binding: FragmentWordsBinding
    private val viewModel: WordsViewModel by viewModels()

    private lateinit var selected: Button

    private val executor = ThreadPerTaskExecutor()

    val channel = Channel<List<String>>()

    private var myMedia: ClickPlayer? = null

    override fun onDestroy() {
        super.onDestroy()
        myMedia?.destroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_words, container, false)

        myMedia = ClickPlayer(requireContext())

        /**
         * Tastatur erzeugen
         *
         * Hier wird die Tastatur erzeugt. Durch das Anpassen von CHARS kann
         * die Tastatur angepasst werden
         */
        for (chunk in CHARS) {
            val row = TableRow(context)

            for (char in chunk) {
                charButtonBinding =
                    DataBindingUtil.inflate(inflater, R.layout.char_button, null, false)
                with(charButtonBinding.root as Button) {
                    var clickListener = {}

                    if (char == "SPACE" || char == "ENTER") {
                        val params = TableRow.LayoutParams()
                        params.span = 2
                        layoutParams = params
                    }

                    /*when (char) {
                        "SPACE" -> {
                            clickListener = {
                                binding.textInput.append(" ")
                                charClicked(this)
                            }
                        }
                        "ENTER" -> {
                            clickListener = {
                                charClicked(this)
                                done()
                            }
                        }
                        else -> {
                            clickListener = {
                                binding.textInput.append(text)
                                charClicked(this)
                            }
                        }
                    }*/
                    text = char.toString()
                    id = "${binding.charTable.childCount}${row.childCount}".toInt()
                    setOnClickListener { clickListener() }
                }
                row.addView(charButtonBinding.root)
            }
            binding.charTable.addView(row)
        }

        /*if (args.mode) {
            // DREA Modus an
            viewModel.mode = 1
        } else {
            // DREA Modus aus
            viewModel.mode = 0
        }*/

        selected = (binding.charTable[0] as TableRow)[0] as Button

        var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
        selBinding!!.sel = true

        return binding.root
    }

    /**
     * Done Funktion
     *
     * Wird ausgeführt, wenn Proband auf "Enter" klickt. Falls kein weiteres Wort mehr
     * verfügbar ist, wird das Ende des Durchlaufes gelogged.
     */
    private fun done() {
        if (viewModel.wordCheck(binding.textInput.text.toString())) {
            wordDone(binding.currentWord.text.toString(), binding.textInput.text.toString())
            binding.textInput.text = ""
            val done = viewModel.getNextWord()
            if (done) {
                binding.textInput.text = "Du bist fertig!"
                trialEnded()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            wordsViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            btnPrevious.setOnClickListener {
                selectNextChar(Direction.LEFT)
                //executor.execute(testRunnable(1))
            }
            btnNext.setOnClickListener {
                selectNextChar(Direction.RIGHT)
                //executor.execute(testRunnable(0))
            }
            btnStart.setOnClickListener {
                viewModel.getNextWord()
                it.visibility = View.GONE
                trialStarted()
            }
        }

        viewModel.viewModelScope.launch {
            channel.consumeEach { message ->
                processMessage(message)
            }
        }

        val mqttClient = DreaMqttClient("tcp://10.0.2.2:1883", MqttClient.generateClientId(), context)
        val piClient = DreaMqttClient("tcp://192.168.178.73:1883", MqttClient.generateClientId(), context)
        //val mqttClient = DreaMqttClient("tcp://hivemq.dock.moxd.io:1883", MqttClient.generateClientId(), context)
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.keepAliveInterval = 5 * 60
        val token = mqttClient.connect(options)
        val piToken = piClient.connect(options)

        piClient.client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d(STUDY_TAG, "Connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = message.toString().split(",")
                    val fingerCount = msg[1].toInt()
                    if (fingerCount == -1) {
                        inputCharacter()
                        return
                    }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                TODO("Not yet implemented")
            }
        })

        mqttClient.client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d(STUDY_TAG, "Connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                viewModel.viewModelScope.launch {
                    val msg = message.toString().split(",")
                    channel.send(msg)
                }

                val msg = message.toString().split(",")
                val fingerCount = msg[1].toInt()
                if (fingerCount == -1) {
                    inputCharacter()
                    return
                }
                //executor.execute(messageRunnable(message))
                /*viewModel.viewModelScope.launch {
                    processMessage(message)
                }*/
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun calcSelected(direction: Direction, fingerCount: Int) {
        var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
        selBinding!!.sel = false
        val indices = viewModel.selectNext(direction, fingerCount)

        selected = (binding.charTable[indices[0]] as TableRow)[indices[1]] as Button
        selectedChar(selected)
        selBinding = DataBindingUtil.findBinding(selected)
        myMedia?.playClickSound()
        selBinding!!.sel = true
    }

    /**
     * Buchstaben eingeben
     *
     * Diese Funktion wird ausgeführt, wenn der Proband einen Buchstaben eingibt.
     * Dabei wird der Text des aktuell ausgewählten Buttons (selected)
     * dem Textfeld angefügt.
     *
     * Wird "SPACE" oder "ENTER" geklickt, so wird der entsprechende alternative
     * Code ausgeführt.
     */
    private fun inputCharacter() {
        var toAppend = ""
        when (selected.text) {
            "SPACE" -> {
                toAppend = " "
            }
            "ENTER" -> {
                done()
            }
            else -> {
                toAppend = selected.text.toString()
            }
        }
        val correct = viewModel.spellCheck(binding.textInput.text.toString(), toAppend)
        if (correct) {
            binding.textInput.append(toAppend)
            charClicked(selected)
        }
    }

    private val accumulator = Accumulator(18.0)

    fun evalAccAction(ts: Long, rotation: Double, fingerCount: Int, resetFunction: (Double, Long) -> Unit) {
        accumulator.reset(ts, rotation, resetFunction)
        accumulator.accumulate(ts, rotation)
        val direction: Direction? = accumulator.checkForGesture()

        if (direction != null) {
            calcSelected(direction, fingerCount)
            //executor.execute(bindingRunnable(direction, fingerCount))
        }
    }

    /**
     * Verarbeitet eine MQTT Nachricht
     *
     * Diese Funktion erhält eine MQTT Nachricht bzw. die in der Nachricht enthaltenen Daten.
     * Daraufhin werden diese Daten verarbeitet und eine enstprechende Aktion ausgeführt.
     *
     * @param msg die in der MQTT Nachricht enthaltenen Daten
     */
    fun processMessage(msg: List<String>) {
        try {
            if (msg.size != 4) return

            val fingerCount = msg[1].toInt()

            if (fingerCount < 2) return

            val ts = msg[0].toLong(10);
            val rotation = msg[2].substring(0, 4).toDouble()

            evalAccAction(ts, -rotation, fingerCount, accumulator.resetWhenSignChanges)
        } catch (e: Exception) {
            e.printStackTrace()
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
                val rotation = msg[2].substring(0, 4).toDouble()

                evalAccAction(ts, rotation, fingerCount, accumulator.resetWhenCounter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun selectNextChar(direction: Direction): Button {
        var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
        selBinding!!.sel = false
        val indices = viewModel.selectNext(direction, finger)

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