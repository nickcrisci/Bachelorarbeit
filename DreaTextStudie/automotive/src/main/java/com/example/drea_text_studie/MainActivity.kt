package com.example.drea_text_studie

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import com.example.drea_text_studie.databinding.ActivityMainBinding
import kotlinx.coroutines.*

val CHARS = listOf(
    ('A'..'G').toList(),
    ('H'..'N').toList(),
    ('O'..'T').toList(),
    ('U'..'Z').toList()
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}