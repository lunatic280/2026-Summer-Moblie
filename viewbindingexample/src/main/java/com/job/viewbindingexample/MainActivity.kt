package com.job.viewbindingexample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.job.viewbindingexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reset.setOnClickListener {
            binding.titleText.text = "Hello Binding Example"
        }

        binding.change.setOnClickListener {
            binding.titleText.text = "텍스트가 변경되었음" }
    }



}
