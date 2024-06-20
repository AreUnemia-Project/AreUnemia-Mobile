package com.dicoding.areunemia.view

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.News
import com.dicoding.areunemia.view.main.NewsDetailActivity

class ListNewsAdapter(private val context: Context, private val listNews: List<News>) : RecyclerView.Adapter<ListNewsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_berita, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listNews.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, description, img, date) = listNews[position]
        holder.imgPhoto.setImageResource(img)
        holder.tvTitle.text = title
        holder.tvDescription.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
        holder.tvDate.text = date

        holder.itemView.setOnClickListener {
            val clickedItem = listNews[position]
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("news_item", clickedItem) // Pass clicked item data
            context.startActivity(intent)
        }
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        val tvDate: TextView = itemView.findViewById(R.id.tv_item_date)
    }
}
