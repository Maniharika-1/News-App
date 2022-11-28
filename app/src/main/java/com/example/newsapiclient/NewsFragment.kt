package com.example.newsapiclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapiclient.data.model.Article
import com.example.newsapiclient.data.util.Resource
import com.example.newsapiclient.databinding.FragmentNewsBinding
import com.example.newsapiclient.presentation.adapter.NewsAdapter
import com.example.newsapiclient.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private var country = "us"
    private var page = 1
    private var isScrolling = false
    private var isLoading = false
    private var isLastPage = false
    private var pages = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

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

        initRecyclerView()
        viewNewsList()
        setSearchView()
    }

    private fun viewNewsList() {
        newsViewModel.getNewsHeadlines(country,page)
        newsViewModel.newsHeadlines.observe(viewLifecycleOwner){response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let{
                        newsAdapter.differ.submitList(it.articles.toList())

                        if(it.totalResults%20 == 0)
                            pages = it.totalResults/20
                        else pages = it.totalResults/20 + 1
                    }

                    isLastPage = page == pages
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(activity, "An error occured : $it", Toast.LENGTH_LONG).show()
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
            val shouldPaginate = !isLoading && !isLastPage && hasReachedToEnd && isScrolling
            if(shouldPaginate) {
                page++
                newsViewModel.getNewsHeadlines(country, page)
                isScrolling = false
            }

        }
    }

    //search
    private fun setSearchView() {
        binding.newsSearchView.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    newsViewModel.searchNews("us", p0.toString(),page)
                    viewSearchedNews()
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    MainScope().launch { //MainScope is coroutine launcher specially created for user interface components.

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
                    initRecyclerView()
                    viewNewsList()
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

                            if(it.totalResults%20 == 0)
                                pages = it.totalResults/20
                            else pages = it.totalResults/20 + 1
                        }

                        isLastPage = page == pages
                    }
                    is Resource.Error -> {
                        response.message?.let {
                            Toast.makeText(activity, "An error occured : $it", Toast.LENGTH_LONG).show()
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