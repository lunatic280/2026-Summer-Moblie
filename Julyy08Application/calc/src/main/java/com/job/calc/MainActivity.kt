package com.job.calc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.job.calc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val defaultPadding = (24 * resources.displayMetrics.density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + defaultPadding,
                systemBars.top + defaultPadding,
                systemBars.right + defaultPadding,
                systemBars.bottom + defaultPadding
            )
            insets
        }

        binding.buttonAdd.setOnClickListener {
            calculate(Operation.ADD)
        }
        binding.buttonSubtract.setOnClickListener {
            calculate(Operation.SUBTRACT)
        }
        binding.buttonMultiply.setOnClickListener {
            calculate(Operation.MULTIPLY)
        }
        binding.buttonDivide.setOnClickListener {
            calculate(Operation.DIVIDE)
        }
        binding.buttonClear.setOnClickListener {
            binding.editTextNumber1.text?.clear()
            binding.editTextNumber2.text?.clear()
            binding.textViewResult.text = "결과: "
        }
    }

    private fun calculate(operation: Operation) {
        val number1 = binding.editTextNumber1.text.toString().toDoubleOrNull()
        val number2 = binding.editTextNumber2.text.toString().toDoubleOrNull()

        if (number1 == null || number2 == null) {
            binding.textViewResult.text = "숫자를 입력하세요."
            return
        }

        if (operation == Operation.DIVIDE && number2 == 0.0) {
            binding.textViewResult.text = "0으로 나눌 수 없습니다."
            return
        }

        val result = when (operation) {
            Operation.ADD -> number1 + number2
            Operation.SUBTRACT -> number1 - number2
            Operation.MULTIPLY -> number1 * number2
            Operation.DIVIDE -> number1 / number2
        }
        binding.textViewResult.text = "결과: ${formatResult(result)}"
    }

    private fun formatResult(result: Double): String {
        return if (result % 1.0 == 0.0) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }

    private enum class Operation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }
}
