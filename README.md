# Studie zur Texteingabe in Autos mit einem neuartigen Drehcontroller
Dieses Repo beinhaltet Code, welchen ich für die Erstellung meiner Bachelorarbeit geschrieben habe. 
In meiner Bachelorarbeit führe ich eine Studie durch, die sich mit der Texteingabe in Autos mit einem neuartigen Drehcontroller auseinandersetzt. Als neuartiger Drehcontroller wird an dieser Stelle ein Drehcontroller bezeichnet, welcher in der Lage ist, zu erkennen, mit wie vielen Fingern er berührt (und gedreht) wird. Dies ermöglicht die Erweiterung der Funktionen des Drehcontrollers.

## DREA
Das Forschungsprojekt, welches sich mit der Entwicklung des Controllers befasst, trägt den Namen "DREA". 
Projektpartner sind: 
- Brehmer GmbH & Co. KG
- Omni Elektronik GmbH
- moxd lab / Cologne Institute for Digital Ecosystems, Technische Hochschule Köln

Gefördert wird das Projekt durch das Bundesministerium für Wirtschaft und Energie, sowie dem ZIM (Zentrales Innovationsprogramm Mittelstand)

Das Projekt DREA selbst befasst sich mit der Entwicklung des Drehcontrollers im Automobile Kontext, also der Anwendung des Controllers im Auto. Allerdings findet der Controller auch darüber hinaus anwendung und wurde daher in einer Veröffentlichung auch als [MultiKnob](https://dl.acm.org/doi/10.1145/3543758.3547561) bezeichnet.
Außerdem wurden bereits von anderen Studenten Ausarbeitungen angefertigt, welche sich mit Anwendungen des Controllers in verschiedenen Kontexten befassen (bspw. die Integration des Controllers in ein Smart-Home System).

## Meine Ausarbeitung
In dieser Ausarbeitung wird, wie der Titel bereits verrät, die Texteingabe mithilfe des Controllers in Autos untersucht. Genauer wird zwischen der Texteingabe mit einem "normalen" Controller und der Texteingabe mittels des DREA verglichen. Die Idee besteht darin, den Probanden die Aufgabe zu geben bestimmte Worte einzugeben.

### Versuchsaufbau 
Die virtuelle Tastatur ist in vier Reihen unterteilt und die Buchstaben lassen sich mittels des Controllers auswählen. Dem Proband wird ein Wort dargestellt, welches er mithilfe des Controllers und der Tastatur einzugeben hat. Hat er das Wort eingegeben, so muss die Eingabe bestätigt werden und das nächste Wort wird dargestellt. Wurden alle Worte eingegeben, so wird ein erneuter Durchlauf gestartet, allerdings wird der Controller "Modus" gewechselt. Wurde zuerst der "normale" Controller verwendet, so muss der Proband den Durchlauf nun mit dem DREA wiederholen und vice versa.

Wird der "normale" Controller verwendet, so gelangt der Proband in die nächste Reihe, indem er über das Ende der vorherigen Reihe "hinausscrollt". Dies bedeutet, dass wenn der letzte Buchstabe einer Reihe ausgewäht ist, und weiter gedreht wird, der erste Buchstabe der nächsten Zeile ausgewählt wird. Dies funktioniert selbstverständlich auch in die andere Richtung. Der Proband kann somit jeden Buchstaben der Tastatur durch das einfache Drehen des Controllers erreichen.

Wird der DREA hingegen verwendet, so bewegt sich der Proband je nachdem mit wie vielen Fingern er am Controller dreht, in der entsprechenden Reihe.
Hierbei gilt:
- 2 Finger entspricht der ersten Reihe
- 3 Finger entspricht der zweiten Reihe
- 4 Finger entspricht der dritten Reihe
- 5 Finger entspricht der vierten Reihe

Wird über das Ende einer Reihe hinausgedreht, so wird nicht wie zuvor die Reihe gewechselt, sondern an den Anfang der selben Reihe gesprungen, sofern der Controller weiterhin mit der entsprechenden Anzahl an Fingern gedreht wird.
Dies ermöglicht einen schnellen Wechsel zwischen den verschiedenen Reihen der Tastatur und somit theoretisch eine schnellere Eingabe von Worten.

### Was wird untersucht
Während die Worte eingegeben werden, werden verschiedene Daten aufgezeichnet:
- Wechsel eines Buchstaben
- Eingabe eines Buchstaben
- Anzeigen eines neuen Wortes
- Bestätigung der Eingabe eines Wortes

Zwar scheint dies nur wenig zu sein, allerdings lassen sich hieraus verschiedene Dinge entnehmen:
- Die Geschwindigkeit mit der zwischen den Buchstaben gewechselt wird
- Die Geschwindigkeit mit der Buchstaben eingegeben werden
- Die Geschwindigkeit mit der ein Wort eingegeben wird

Dadurch kann untersucht werden, ob die Probanden durch den DREA im Stande sind, nicht nur Buchstaben, sondern ganze Worte schneller und effizienter einzugeben.
