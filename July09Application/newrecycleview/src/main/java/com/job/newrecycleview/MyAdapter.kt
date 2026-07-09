package com.job.newrecycleview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.job.newrecycleview.databinding.ItemMainBinding

class MyAdapter(private val data: MutableList<String>): androidx.recyclerview.widget.RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ItemMainBinding): androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        val textView: android.widget.TextView = binding.itemData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item
        holder.binding.itemRoot.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Clicked: $item", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addItem(item: String): Int {
        data.add(item)
        val position = data.lastIndex
        notifyItemInserted(position)
        return position
    }

    fun removeLastItem(): Boolean {
        if (data.isEmpty()) {
            return false
        }

        val position = data.lastIndex
        data.removeAt(position)
        notifyItemRemoved(position)
        return true
    }
}
