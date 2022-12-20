package com.example.drea_text_studie

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources

val CHARS = listOf(
    ('A'..'G').toList(),
    ('H'..'N').toList(),
    ('O'..'T').toList(),
    ('U'..'Z').toList()
)

class MainActivity : AppCompatActivity() {

    lateinit var SELECTED_DRAWABLE: Drawable
    val UNSELECTED_DRAWABLE = ColorDrawable(Color.BLACK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SELECTED_DRAWABLE = AppCompatResources.getDrawable(this, R.drawable.selected)!!

        val inputTextView: TextView = findViewById(R.id.textInput)
        inputTextView.text = ""
        val table: TableLayout = findViewById(R.id.charTable)

        for (chunk in CHARS) {
            val row = TableRow(this)

            for (char in chunk) {
                val button: Button = layoutInflater.inflate(R.layout.char_button, null) as Button
                with(button) {
                    text = char.toString()
                    id = "${table.childCount}${row.childCount}".toInt()
                    setOnClickListener {
                        inputTextView.append(text)
                    }
                }
                row.addView(button)
            }
            table.addView(row)
        }
    }

    var rowCounter = 0
    var itemCounter = 0
    private var selected: Button? = null
    fun selectNext(view: View) {
        if (selected != null) {
            selected!!.background = UNSELECTED_DRAWABLE
        }
        selected = findViewById("${rowCounter}${itemCounter}".toInt())
        val parentRow = selected!!.parent as TableRow
        selected!!.background = SELECTED_DRAWABLE

        // Wenn letztes Item in Reihe
        if (itemCounter == (parentRow.childCount - 1)) {
            // Wenn letzte Reihe in Tabelle
            if (rowCounter == 3) {
                // Dann reset auf Reihe 0 und Item 0
                rowCounter = 0
                itemCounter = 0
            } else {
                // Sonst nächste Reihe und Item auf 0
                itemCounter = 0
                rowCounter++
            }
        } else {
            // Sonst nächstes Item
            itemCounter++
        }
    }
}