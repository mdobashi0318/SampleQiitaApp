package com.example.sampleqiitaapp

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleqiitaapp.adapter.ArticleAdapter
import com.example.sampleqiitaapp.data.Article
import com.example.sampleqiitaapp.databinding.FragmentArticleListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding
    private var date: LocalDateTime? = null
    private var articles: List<Article> = listOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleListBinding.inflate(layoutInflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        swipeRefresh()

        if (date == null || ChronoUnit.MINUTES.between(date, LocalDateTime.now()) >= 5) {
            date = LocalDateTime.now()
            getArticle()
        } else {
            setAdapter()
        }

        return binding.root
    }

    private fun getArticle() {
        visibleProgressBar()
        APIManager.get<Article>("items", {
            articles = it
            setAdapter()
            goneProgressBar()
        }) {
            goneProgressBar()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("通信に失敗しました")
                .setMessage("再接続しますか?")
                .setPositiveButton("再接続") { _, _ ->
                    getArticle()
                }
                .setNegativeButton("閉じる") { _, _ -> }
                .show()
        }
    }


    /**
    ProgressBarを表示させ、画面操作を無効にする
     **/
    private fun visibleProgressBar() {
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.progressBar.visibility = View.VISIBLE
        }
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    /**
    ProgressBarを消し、画面操作を有効にする
     **/
    private fun goneProgressBar() {
        binding.progressBar.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }


    private fun swipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getArticle()
        }
    }

    private fun setAdapter() {
        val adapter = ArticleAdapter(articles)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ArticleAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, url: String) {
                view.findNavController().navigate(
                    ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment(
                        url
                    )
                )
            }
        })
    }
}