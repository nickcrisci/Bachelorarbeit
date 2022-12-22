package com.example.drea_text_studie.ui.studie

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.drea_text_studie.R
import com.example.drea_text_studie.databinding.FragmentWordsBinding

val CHARS = listOf(
    ('A'..'G').toList(),
    ('H'..'N').toList(),
    ('O'..'T').toList(),
    ('U'..'Z').toList()
)

class WordsFragment : Fragment() {

    private lateinit var binding: FragmentWordsBinding
    private val viewModel: WordsViewModel by viewModels()

    lateinit var SELECTED_DRAWABLE: Drawable
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
                Log.i("Nick", char.toString())
                val button: Button = layoutInflater.inflate(R.layout.char_button, null) as Button
                with(button) {
                    text = char.toString()
                    id = "${binding.charTable.childCount}${row.childCount}".toInt()
                    setOnClickListener {
                        binding.textInput.append(text)
                        Log.i("Study", "Button clicked: $text")
                    }
                }
                row.addView(button)
            }
            binding.charTable.addView(row)
        }

        selected = (binding.charTable[0] as TableRow)[0] as Button
        selected.background = SELECTED_DRAWABLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.wordsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.nextWord.setOnClickListener {
            viewModel.getNextWord()
        }
        viewModel.getNextWord()
        binding.btnNext.setOnClickListener{
            selectNext()
        }
    }

    var rowCounter = 0
    var itemCounter = 1
    fun selectNext() {
        selected.background = UNSELECTED_DRAWABLE

        selected = (binding.charTable[rowCounter] as TableRow)[itemCounter] as Button
        val parentRow = selected.parent as TableRow
        selected.background = SELECTED_DRAWABLE

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

