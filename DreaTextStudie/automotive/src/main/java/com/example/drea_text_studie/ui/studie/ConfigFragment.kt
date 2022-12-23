package com.example.drea_text_studie.ui.studie

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.drea_text_studie.R
import com.example.drea_text_studie.databinding.FragmentConfigBinding
import com.example.drea_text_studie.databinding.FragmentWordsBinding
import com.example.drea_text_studie.ui.studie.ConfigFragmentDirections.ActionConfigFragmentToWordsFragment4
import com.example.drea_text_studie.ui.studie.ConfigFragmentDirections.actionConfigFragmentToWordsFragment4
import com.example.drea_text_studie.util.Sex

class ConfigFragment : Fragment() {

    private lateinit var binding: FragmentConfigBinding
    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_config, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSwitch.setOnClickListener {
            var action: ConfigFragmentDirections.ActionConfigFragmentToWordsFragment4
            with(binding) {
                val firstname = firstName.editText!!.text.toString()
                val surname = surname.editText!!.text.toString()
                val age = age.editText!!.text.toString().toInt()
                val mode = mode.isChecked
                val checkedId = sexRadio.checkedRadioButtonId
                val sex = when(view.findViewById<RadioButton>(checkedId).text) {
                    "Männlich" -> Sex.valueOf("Male")
                    "Weiblich" -> Sex.valueOf("Female")
                    else -> Sex.valueOf("Diverse")
                }
                action = actionConfigFragmentToWordsFragment4(
                    mode, sex, firstname, surname, age
                )
            }
            Navigation.findNavController(view).navigate(action)
        }
    }
}