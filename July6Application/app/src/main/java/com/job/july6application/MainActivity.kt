package com.job.july6application

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.job.july6application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var initTime = 0L
    private var elapsedTime = 0L
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            if (SystemClock.elapsedRealtime() - initTime < 3000) {
                finish()
            } else {
                initTime = SystemClock.elapsedRealtime()
                Toast.makeText(this@MainActivity, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
        }

        elapsedTime = savedInstanceState?.getLong(KEY_ELAPSED_TIME) ?: 0L
        isRunning = savedInstanceState?.getBoolean(KEY_IS_RUNNING) ?: false

        binding.chronmeter.base = if (isRunning) {
            savedInstanceState?.getLong(KEY_BASE_TIME) ?: SystemClock.elapsedRealtime() - elapsedTime
        } else {
            SystemClock.elapsedRealtime() - elapsedTime
        }

        if (isRunning) {
            binding.chronmeter.start()
        }
        updateStopButtonState()

        binding.start.setOnClickListener {
            if (isRunning) return@setOnClickListener

            binding.chronmeter.base = SystemClock.elapsedRealtime() - elapsedTime
            binding.chronmeter.start()
            isRunning = true
            updateStopButtonState()
            Toast.makeText(this, "시간 측정을 시작합니다.", Toast.LENGTH_SHORT).show()
        }

        binding.stop.setOnClickListener {
            if (!isRunning) return@setOnClickListener

            binding.chronmeter.stop()
            elapsedTime = SystemClock.elapsedRealtime() - binding.chronmeter.base
            isRunning = false
            updateStopButtonState()
            Toast.makeText(this, "시간 측정을 멈춥니다.", Toast.LENGTH_SHORT).show()
        }

        binding.reset.setOnClickListener {
            binding.chronmeter.stop()
            elapsedTime = 0L
            isRunning = false
            binding.chronmeter.base = SystemClock.elapsedRealtime()
            updateStopButtonState()
            Toast.makeText(this, "측정 시간을 초기화합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStopButtonState() {
        binding.stop.isEnabled = isRunning
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong(KEY_ELAPSED_TIME, elapsedTime)
        outState.putBoolean(KEY_IS_RUNNING, isRunning)
        outState.putLong(KEY_BASE_TIME, binding.chronmeter.base)
    }

    companion object {
        private const val KEY_ELAPSED_TIME = "elapsed_time"
        private const val KEY_IS_RUNNING = "is_running"
        private const val KEY_BASE_TIME = "base_time"
    }
}
