package com.example.sampleqiitaapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

        visibleProgressBar()
        APIManager.get<Article>("items", {
            val adapter = ArticleAdapter(it)
            binding.recyclerView.adapter = adapter
            goneProgressBar()
        }) {
            // TODO: 通信失敗のダイアログを出す
            goneProgressBar()
        }

        return binding.root
    }


    /**
    ProgressBarを表示させ、画面操作を無効にする
     **/
    private fun visibleProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    /**
    ProgressBarを消し、画面操作を有効にする
     **/
    private fun goneProgressBar() {
        binding.progressBar.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}