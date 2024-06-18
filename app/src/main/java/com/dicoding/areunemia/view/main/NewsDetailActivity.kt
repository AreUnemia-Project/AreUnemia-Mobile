package com.dicoding.areunemia.view.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.News

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail) // Replace with your layout resource

        val intent = intent
        val clickedItem = intent.getParcelableExtra<News>("news_item")

        if (clickedItem != null) {
            val tvTitle = findViewById<TextView>(R.id.tv_Title) // Replace with your view IDs
            val tvDescription = findViewById<TextView>(R.id.tv_Desc)
            val imgPhoto = findViewById<ImageView>(R.id.imageView)

            tvTitle.text = clickedItem.title
            tvDescription.text = clickedItem.description
            imgPhoto.setImageResource(clickedItem.img) // Assuming image is an integer resource
        }
    }
}
