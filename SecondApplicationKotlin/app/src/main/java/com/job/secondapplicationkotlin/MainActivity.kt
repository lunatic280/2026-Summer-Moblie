package com.job.secondapplicationkotlin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val worldCupImage = findViewById<ImageView>(R.id.worldCupImage)
        Glide.with(this)
            .load("https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/FIFA_World_Cup_Trophy_%28Ank_Kumar%2C_Infosys_Limited%29_04.jpg/250px-FIFA_World_Cup_Trophy_%28Ank_Kumar%2C_Infosys_Limited%29_04.jpg")
            .into(worldCupImage)
    }
}