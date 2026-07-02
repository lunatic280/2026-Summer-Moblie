package com.job.visibleclick

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val visibleButton: Button = findViewById(R.id.button4)
        val invisibleButton: Button = findViewById(R.id.button5)
        val goneButton: Button = findViewById(R.id.button6)
        val targetButton : Button = findViewById(R.id.button10)

        visibleButton.setOnClickListener {
            targetButton.visibility = Button.VISIBLE
        }
        invisibleButton.setOnClickListener {
            targetButton.visibility = Button.INVISIBLE }
        goneButton.setOnClickListener {
            targetButton.visibility = Button.GONE }
    }
}