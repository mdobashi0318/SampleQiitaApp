package com.example.sampleqiitaapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.sampleqiitaapp.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {

    private lateinit var binding: FragmentArticleDetailBinding

    private val naviArgs: ArticleDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleDetailBinding.inflate(layoutInflater, container, false)
        binding.webView.loadUrl(naviArgs.url)

        return binding.root
    }


}