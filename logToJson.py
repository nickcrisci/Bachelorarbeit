import os.path
import json
import sys

arguments = sys.argv
name = arguments[1]
mode = arguments[2]
date = arguments[3]

def convertToJson(name, mode, date):
    jsonLogName = f"{date}_{name}_{mode}_log"
    path = "Logs/" + jsonLogName + ".json"
    with open("log.txt", "r") as file:
        for line in file:
            if "beginning" in line:
                continue
            sanitized = line.strip()
            sanitized = " ".join(sanitized.split())
            sanitized = sanitized.replace("):", ";")
            
            data = sanitized.split(";")
            data[0] = data[0].replace(" ", ";").split(";")
            
            jsonData = json.loads(data[1])
            jsonData["timestamp"] = data[0][1]
            
            if not os.path.exists(path):
                with open(path, "w") as jsonFile:
                    jsonFile.write('{"events": [')
            
            with open(path, "a") as jsonFile:
                jsonFile.write(json.dumps(jsonData, indent=4))
                jsonFile.write(",")
                                
        with open(path, "a") as jsonFile:
            jsonFile.write("]}")

convertToJson(name, mode, date)