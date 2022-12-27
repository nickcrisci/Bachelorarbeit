package com.example.drea_text_studie.ui.studie

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.drea_text_studie.R
import com.example.drea_text_studie.databinding.CharButtonBinding
import com.example.drea_text_studie.databinding.FragmentWordsBinding
import com.example.drea_text_studie.util.Direction
import com.example.drea_text_studie.util.charClicked
import com.example.drea_text_studie.util.selectedChar
import com.example.drea_text_studie.util.wordDone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.math.abs

val CHARS = listOf(
    (1..9).plus(0),
    ('A'..'J').toList(),
    ('K'..'T').toList(),
    ('U'..'Z').toList()
)

class WordsFragment : Fragment() {

    private val STUDY_TAG = "Study"
    private val args: WordsFragmentArgs by navArgs()

    private val treshold = 5.0
    private var lastMsgReceived: Long = 0

    private lateinit var charButtonBinding: CharButtonBinding

    private lateinit var binding: FragmentWordsBinding
    private val viewModel: WordsViewModel by viewModels()

    private lateinit var SELECTED_DRAWABLE: Drawable
    private lateinit var selected: Button
    val UNSELECTED_DRAWABLE = ColorDrawable(Color.BLACK)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_words, container, false)

        SELECTED_DRAWABLE = AppCompatResources.getDrawable(requireContext(), R.drawable.selected)!!
        for (chunk in CHARS) {
            val row = TableRow(context)
            for (char in chunk) {
                charButtonBinding = DataBindingUtil.inflate(inflater, R.layout.char_button, null, false)
                //val button: Button = layoutInflater.inflate(R.layout.char_button, null) as Button
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
       // selected.background = SELECTED_DRAWABLE

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

        val clientId = MqttClient.generateClientId()
        val serverUri = "tcp://10.0.2.2:1883"
        val client = MqttAndroidClient(context, serverUri, clientId)
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.keepAliveInterval = 1200
        val token = client.connect(options)
        token.actionCallback = object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                val subToken = client.subscribe("drea", 0)
                Log.d(STUDY_TAG, "Connected!")
            }
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(STUDY_TAG, exception.toString())
            }
        }

        client.setCallback(object: MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d(STUDY_TAG, "Connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                try {
                    val msg = message.toString().split(",")
                    if (msg.size != 4) return

                    val ts = msg[0].toLong(10);
                    if ((ts - lastMsgReceived) < 2.5 * 1000) return
                    val fingerCount = msg[1].toInt()

                    if (fingerCount < 2) return

                    val rotation = msg[2].substring(0, 3).toDouble()

                    if (rotation < 2.0 && rotation > -2.0) return
                    if (rotation > treshold) {
                        selectNextChar(Direction.RIGHT)
                    }
                    if (rotation < -treshold) {
                        selectNextChar(Direction.LEFT)
                    }


                    //Log.d(STUDY_TAG, "Time: $ts, Fingers: $fingerCount, Rotation: $rotation")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun selectNextChar(direction: Direction): Button {
        var selBinding = DataBindingUtil.getBinding<CharButtonBinding>(selected)
        selBinding!!.sel = false
        //selected.background = UNSELECTED_DRAWABLE
        val indices = viewModel.selectNext(direction)

        selected = (binding.charTable[indices[0]] as TableRow)[indices[1]] as Button
        selectedChar(selected)
        selBinding = DataBindingUtil.findBinding<CharButtonBinding>(selected)
        selBinding!!.sel = true
        //selected.background = SELECTED_DRAWABLE
        return selected
    }
}

