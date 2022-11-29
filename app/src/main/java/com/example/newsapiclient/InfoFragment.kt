package com.example.newsapiclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.newsapiclient.databinding.FragmentInfoBinding
import com.example.newsapiclient.presentation.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding
    private lateinit var viewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        val args : InfoFragmentArgs by navArgs()
        val selectedArticle = args.selectedArticle
        binding.newsInfoWebView.apply {
            webViewClient = WebViewClient()
            if(selectedArticle.url != null)
                loadUrl(selectedArticle.url)
        }

        binding.saveFAB.setOnClickListener {
            viewModel.saveArticle(selectedArticle)
            Snackbar.make(view,"Saved Successfully!", Snackbar.LENGTH_LONG).show()
        }


    }
}