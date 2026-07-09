package com.job.fragmentapp

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.job.fragmentapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firstBtn.visibility = View.INVISIBLE

        binding.firstBtn.setOnClickListener {
            binding.firstBtn.visibility = View.VISIBLE
            binding.secondBtn.visibility = View.INVISIBLE
            binding.thridBtn.visibility = View.INVISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BlankFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.secondBtn.setOnClickListener {
            binding.firstBtn.visibility = View.INVISIBLE
            binding.secondBtn.visibility = View.VISIBLE
            binding.thridBtn.visibility = View.INVISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BlankFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.thridBtn.setOnClickListener {
            binding.firstBtn.visibility = View.INVISIBLE
            binding.secondBtn.visibility = View.INVISIBLE
            binding.thridBtn.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BlankFragment())
                .addToBackStack(null)
                .commit()
        }

    }
}