package com.example.newsapiclient

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapiclient.data.model.Article
import com.example.newsapiclient.data.util.Resource
import com.example.newsapiclient.databinding.FragmentNewsBinding
import com.example.newsapiclient.databinding.NewsListItemBinding
import com.example.newsapiclient.presentation.adapter.NewsAdapter
import com.example.newsapiclient.presentation.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NewsFragment : Fragment() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private var country = "us"
    private var page = 1
    private var isScrolling = false
    private var isLoading = false
    private var isLastPage = false
    private var pages = 0
    private var allArticles = ArrayList<Article>()
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewsBinding.bind(view)
        newsViewModel = (activity as MainActivity).viewModel
        newsAdapter = (activity as MainActivity).newsAdapter

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("selected_article", it) //To pass article as Serializable, make Article and Source data classes
                                                        // implement Serializable interface
            }

            findNavController().navigate(R.id.action_newsFragment_to_infoFragment, bundle)
        }

        newsAdapter.setOnFavItemClickListener {
            newsViewModel.saveArticle(it)
            Snackbar.make(view, "Added to favorites!", Snackbar.LENGTH_LONG).show()
        }

        setDate()
        initRecyclerView()
        newsViewModel.getNewsHeadlines(country,page)
        viewNewsList()
        setSearchView()
    }

    private fun showSearchView() {
        binding.headersCL.visibility = View.GONE
        binding.newsSearchView.visibility = View.VISIBLE
    }

    private fun hideSearchView() {
        binding.headersCL.visibility = View.VISIBLE
        binding.newsSearchView.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate() {

        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val now = LocalDateTime.now().format(dateTimeFormatter)

        val parsedDate = LocalDate.parse(now, dateTimeFormatter)
        val dayOfWeek = parsedDate.dayOfWeek.toString()
        val formattedDayOfWeek = "${dayOfWeek[0]}${dayOfWeek.substring(1, dayOfWeek.length).lowercase()}"
        val month = parsedDate.month.toString()
        val formattedMonth = "${month[0]}${month.substring(1, month.length).lowercase()}"
        binding.dateTV.text = "$formattedDayOfWeek, $formattedMonth ${parsedDate.dayOfMonth}"

    }

    private fun viewNewsList() {
        newsViewModel.newsHeadlines.observe(viewLifecycleOwner){response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let{

                        allArticles.addAll(it.articles.toList())
                        newsAdapter.differ.submitList(allArticles)

                        if(it.totalResults%20 == 0)
                            pages = it.totalResults/20
                        else pages = it.totalResults/20 + 1
                    }

                    isLastPage = page == pages
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred : $it", Toast.LENGTH_LONG).show()
                        Log.d("NewsFragmentttt", "viewNewsList: $it, ${response.message}")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun initRecyclerView() {
        //newsAdapter = NewsAdapter() - this is not a best practice. Hence create AdapterModule and provide news adapter instance.
        binding.newsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

            addOnScrollListener(this@NewsFragment.onScrollListener)
        }
    }

    private fun showProgressBar() {
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        isLoading = false
        binding.progressBar.visibility = View.GONE
    }

    private val onScrollListener = object: RecyclerView.OnScrollListener() {

        //Callback method to be invoked when RecyclerView's scroll state changes.
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        //called after scrolling is completed
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.newsRecyclerView.layoutManager as LinearLayoutManager
            val sizeOfCurrentList = layoutManager.itemCount
            val visibleItems = layoutManager.childCount
            val top = layoutManager.findFirstVisibleItemPosition()

            val hasReachedToEnd = top + visibleItems >= sizeOfCurrentList

            val shouldPaginate = !isLoading && !isLastPage && hasReachedToEnd && isScrolling && !isSearching
            if(shouldPaginate) {
                page++
                showProgressBar()
                newsViewModel.getNewsHeadlines(country, page)
                hideProgressBar()
                isScrolling = false
            }
        }
    }

    //search
    private fun setSearchView() {
        binding.newsSearchView.setOnSearchClickListener {
            binding.headersCL.visibility = View.GONE
        }
        binding.newsSearchView.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    page = 1
                    isSearching = true
                    newsViewModel.searchNews("us", p0.toString(),page)
                    viewSearchedNews()
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    MainScope().launch { //MainScope is coroutine launcher specially created for user interface components.
                        isSearching = true
                        delay(20000) //we don't want to update list immediately after user enters a letter. We're giving user
                        // 2 seconds of time to enter the search query.
                        newsViewModel.searchNews("us", p0.toString(),page)
                        viewSearchedNews()

                    }
                    return false
                }

            })

        binding.newsSearchView.setOnCloseListener(
            object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    isSearching = false
                    page = 1
                    //hideSearchView()
                    //initRecyclerView()
                    //viewNewsList()
                    newsViewModel.getNewsHeadlines(country,page)
                    viewNewsList()
                    binding.headersCL.visibility = View.VISIBLE
                    return false
                }

            })
    }

    private fun viewSearchedNews() {
        if(view != null) {

            newsViewModel.searchedNews.observe(viewLifecycleOwner){response ->
                when(response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let{
                            newsAdapter.differ.submitList(it.articles.toList())

//                            if(it.totalResults%20 == 0)
//                                pages = it.totalResults/20
//                            else pages = it.totalResults/20 + 1
                        }

                        //isLastPage = page == pages
                    }
                    is Resource.Error -> {
                        response.message?.let {
                            Toast.makeText(activity, "An error occurred : $it", Toast.LENGTH_LONG).show()
                            Log.d("NewsFragmentttt", "viewSearchedNews: $it, ${response.message}")
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }

        }
    }
}