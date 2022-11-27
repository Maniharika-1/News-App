package com.example.newsapiclient.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapiclient.data.model.Article
import com.example.newsapiclient.databinding.NewsListItemBinding

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

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
        fun bind(article: Article) {

            //set data in text views
            binding.titleTV.text = article.title
            binding.descriptionTV.text = article.description
            binding.publishedAtTV.text = article.publishedAt
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

        }

    }

    private var onItemClickListener: ((Article)->Unit)? = null

    fun setOnItemClickListener(listener: (Article)->Unit) {
        onItemClickListener = listener
    }

}