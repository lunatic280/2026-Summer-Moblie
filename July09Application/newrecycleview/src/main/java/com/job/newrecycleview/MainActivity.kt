package com.job.newrecycleview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.job.newrecycleview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MyAdapter
    private var nextItemNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = mutableListOf<String>()
        for (i in 1..100) {
            data.add("Item $i")
        }
        nextItemNumber = data.size + 1
        adapter = MyAdapter(data)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.addBtn.setOnClickListener {
            val position = adapter.addItem("Item $nextItemNumber")
            nextItemNumber++
            binding.recyclerView.scrollToPosition(position)
        }

        binding.deleteBtn.setOnClickListener {
            val removed = adapter.removeLastItem()
            if (!removed) {
                Toast.makeText(this, "삭제할 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
