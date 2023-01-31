import grovepi

import paho.mqtt.client as mqtt

button = 3

grovepi.pinMode(button, "INPUT")

old, current = 0, 0, 0

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    client.subscribe("drea")

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    client.subscribe("drea")

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("localhost", 1883, 60)

client.loop_start()

data = "0,-1,0,0"

while True:
    try:
        current = grovepi.digitalRead(button)
        if current != old:
            if current == 1:
                client.publish("drea", data)
            old = current
    except IOError:
        print("Error")