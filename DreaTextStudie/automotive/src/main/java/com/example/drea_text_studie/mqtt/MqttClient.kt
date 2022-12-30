package com.example.drea_text_studie.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions

const val TAG = "MQTT"

class DreaMqttClient(serverUri: String, clientId: String, context: Context?) {
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