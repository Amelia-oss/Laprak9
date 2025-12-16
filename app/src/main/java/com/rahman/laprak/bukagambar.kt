package com.rahman.laprak

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import coil.load


class bukagambar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bukagambar)
        // In the source Activity (e.g., MainActivity)
        val imageView = findViewById<ImageView>(R.id.imageView2)
        val link = intent.getStringExtra("key1")
        // Use the message (e.g., set it to a TextView)
        imageView.load(link) {}


    }
}