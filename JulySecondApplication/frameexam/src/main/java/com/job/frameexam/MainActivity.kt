package com.job.frameexam

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import com.job.frameexam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dog.setOnClickListener {
            binding.dogView.visibility = View.VISIBLE
            binding.catView.visibility = View.INVISIBLE
        }

        binding.cat.setOnClickListener {
            binding.catView.visibility = View.VISIBLE
            binding.dogView.visibility = View.INVISIBLE
        }

    }
}