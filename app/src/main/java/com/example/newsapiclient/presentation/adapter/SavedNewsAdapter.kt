package com.example.newsapiclient.presentation.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapiclient.MainActivity
import com.example.newsapiclient.NewsFragment
import com.example.newsapiclient.R
import com.example.newsapiclient.data.model.Article
import com.example.newsapiclient.databinding.NewsListItemBinding
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class SavedNewsAdapter: RecyclerView.Adapter<SavedNewsAdapter.NewsViewHolder>() {

    //with notifyDataSetChanged(), recycler view will not know which element has been modified and will load/refresh all the
    //visible items again which is an inefficient way when data is huge.
    //To avoid this, we are using DiffUtil utility class which calculates the difference between old and new lists and returns
    //the difference.
    private val callback = object:DiffUtil.ItemCallback<Article>(){

        //Called by the DiffUtil to decide whether two object represent the same Item.
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url //compare the unique property
        }

        // Called by the DiffUtil when it wants to check whether two items have the same data. This will compare whole object.
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    //AsyncListDiffer will calculate the difference in background thread. When the data is huge, calculating difference between lists
    //on main thread is an inefficient way. Hence use AsyncListDiffer which runs on background thread
    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val listItemBinding = NewsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(listItemBinding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //passing list item binding object as constructor parameter to set the data in list item views
    inner class NewsViewHolder(val binding: NewsListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        //call this function within onBindViewHolder() to bind data (article) with list item view
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(article: Article) {

            binding.favoriteIB.visibility = View.GONE

            //set data in text views
            binding.titleTV.text = article.title
            binding.descriptionTV.text = article.description
            val date = getFormattedDate(article.publishedAt)
            binding.publishedAtTV.text = date
            binding.sourceTV.text = article.source?.name

            //load news article image using glide
            Glide.with(binding.articleIV.context) //passing image view context, not application context
                .load(article.urlToImage)
                .into(binding.articleIV)

            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }

            binding.favoriteIB.setOnClickListener {
                binding.favoriteIB.setImageResource(R.drawable.ic_baseline_favorite_fill)
                onFavItemClickListener?.let {
                    it(article)
                }

            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getFormattedDate(publishedAt: String?): String {

            val instant = Instant.parse(publishedAt)
            val articlePublishedZonedTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

            return articlePublishedZonedTime.format(dateFormatter)
        }

    }

    //lambda function
    private var onItemClickListener: ((Article)->Unit)? = null

    fun setOnItemClickListener(
        //lambda function as function parameter
        listener: (Article)->Unit
    ) {
        onItemClickListener = listener
    }

    //lambda function
    private var onFavItemClickListener: ((Article)->Unit)? = null

    fun setOnFavItemClickListener(
        //lambda function as function parameter
        listener: (Article)->Unit
    ) {
        onFavItemClickListener = listener
    }

}