package com.job.julyy08application


import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.job.julyy08application.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendar = Calendar.getInstance()

        binding.button1.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    binding.textView.text = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.button2.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    binding.textView.text = "%02d:%02d".format(hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.button3.setOnClickListener {
            showDialog()
        }

        binding.button4.setOnClickListener {
            AlertDialog.Builder(this).run {
                setTitle("알림 상자")
                setMessage("알림 상자입니다.")
                setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        val contentView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 40, 48, 32)
        }
        val messageView = TextView(this).apply {
            text = "Dialog입니다."
            textSize = 18f
        }
        val closeButton = Button(this).apply {
            text = "닫기"
            setOnClickListener {
                dialog.dismiss()
            }
        }

        contentView.addView(
            messageView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        contentView.addView(
            closeButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        dialog.setTitle("Dialog")
        dialog.setContentView(contentView)
        dialog.show()
    }
}
