package com.example.sampleqiitaapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleqiitaapp.adapter.ArticleAdapter
import com.example.sampleqiitaapp.data.Article
import com.example.sampleqiitaapp.databinding.FragmentArticleListBinding

class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleListBinding.inflate(layoutInflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)

        APIManager.get<Article>("items", {
            val adapter = ArticleAdapter(it)
            binding.recyclerView.adapter = adapter
        }) {
            // TODO: 通信失敗のダイアログを出す
        }




        return binding.root
    }


}