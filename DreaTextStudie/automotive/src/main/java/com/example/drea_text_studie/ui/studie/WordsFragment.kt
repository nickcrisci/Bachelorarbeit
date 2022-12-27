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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.drea_text_studie.R
import com.example.drea_text_studie.databinding.FragmentWordsBinding
import com.example.drea_text_studie.util.Direction
import com.example.drea_text_studie.util.charClicked
import com.example.drea_text_studie.util.selectedChar
import com.example.drea_text_studie.util.wordDone

val CHARS = listOf(
    (1..9).plus(0),
    ('A'..'J').toList(),
    ('K'..'T').toList(),
    ('U'..'Z').toList()
)

class WordsFragment : Fragment() {

    private val STUDY_TAG = "Study"
    private val args: WordsFragmentArgs by navArgs()

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
                val button: Button = layoutInflater.inflate(R.layout.char_button, null) as Button
                with(button) {
                    text = char.toString()
                    id = "${binding.charTable.childCount}${row.childCount}".toInt()
                    setOnClickListener {
                        binding.textInput.append(text)
                        charClicked(this)
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
    }

    private fun selectNextChar(direction: Direction): Button {
        selected.background = UNSELECTED_DRAWABLE
        val indicies = viewModel.selectNext(direction)

        selected = (binding.charTable[indicies[0]] as TableRow)[indicies[1]] as Button
        selected.background = SELECTED_DRAWABLE
        return selected
    }
}

