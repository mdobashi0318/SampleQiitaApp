package com.example.sampleqiitaapp.screen

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleqiitaapp.APIManager
import com.example.sampleqiitaapp.R
import com.example.sampleqiitaapp.adapter.ArticleAdapter
import com.example.sampleqiitaapp.data.Article
import com.example.sampleqiitaapp.databinding.FragmentArticleListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding
    private var date: LocalDateTime? = null
    private var articles: List<Article> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleListBinding.inflate(layoutInflater, container, false)
        addMenu()
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


    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.article_list_menu, menu)
            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.bookmark -> {
                        view?.findNavController()?.navigate(
                            ArticleListFragmentDirections.actionArticleListFragmentToBookmarkListFragment()
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun getArticle() {
        visibleProgressBar()
        APIManager.get<List<Article>>("items", {
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
            override fun onItemClickListener(
                view: View,
                position: Int,
                id: String,
                url: String,
                title: String
            ) {
                view.findNavController().navigate(
                    ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment(
                        id,
                        url,
                        title
                    )
                )
            }
        })
    }
}