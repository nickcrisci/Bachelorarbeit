package com.example.drea_text_studie.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.concurrent.Executor
import kotlin.math.abs

const val TAG = "MQTT"

class DreaMqttClient(serverUri: String, clientId: String, context: Context?) {
    private var lastMsgReceived: Long = 0
    private var rotationAcc: Double = 0.0
    private val threshold = 36.0
    val client = MqttAndroidClient(context, serverUri, clientId)

    fun connect(options: MqttConnectOptions): IMqttToken {
        val token = client.connect(options)
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                val subToken = client.subscribe("drea", 0)
                Log.d(TAG, "Connected!")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(TAG, exception.toString())
            }
        }
        return token
    }
}