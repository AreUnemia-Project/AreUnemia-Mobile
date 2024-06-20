package com.dicoding.areunemia.view.main

import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.data.local.pref.News
import com.dicoding.areunemia.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val clickedItem = intent.getParcelableExtra<News>("news_item")

        clickedItem?.let { clickedItem ->
            setupView(clickedItem)
        }

    }

    private fun setupView(clickedItem: News) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        binding.apply {
            tvDate.text = clickedItem.date
            tvTitle.text = clickedItem.title
            tvDesc.text = Html.fromHtml(clickedItem.description, Html.FROM_HTML_MODE_LEGACY)
            imageView.setImageResource(clickedItem.img)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
