package com.example.sampleqiitaapp.screen

import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sampleqiitaapp.APIManager
import com.example.sampleqiitaapp.data.Bookmark
import com.example.sampleqiitaapp.QiitaApplication
import com.example.sampleqiitaapp.R
import com.example.sampleqiitaapp.data.Article
import com.example.sampleqiitaapp.databinding.FragmentArticleDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ArticleDetailFragment : Fragment() {

    private lateinit var binding: FragmentArticleDetailBinding

    private val naviArgs: ArticleDetailFragmentArgs by navArgs()

    private val dao = QiitaApplication.database.bookmarkDao()

    private var bookmark: Bookmark? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentArticleDetailBinding.inflate(layoutInflater, container, false)
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(naviArgs.url)
        getBookmark()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        updateBookmark()
    }


    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
                CoroutineScope(Dispatchers.Main).launch {
                    if (bookmark == null) {
                        menu.getItem(0).isVisible = true
                        menu.getItem(1).isVisible = false
                    } else {
                        menu.getItem(0).isVisible = false
                        menu.getItem(1).isVisible = true
                    }
                }
            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val date = nowStr()
                when (menuItem.itemId) {
                    R.id.add_bookmark -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            dao.add(
                                Bookmark(
                                    naviArgs.id,
                                    naviArgs.title,
                                    naviArgs.url,
                                    date,
                                    date
                                )

                            )
                            CoroutineScope(Dispatchers.Main).launch {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("ブックマークに追加しました。")
                                    .setPositiveButton(R.string.ok) { _, _ ->
                                        getBookmark()
                                    }
                                    .show()
                            }
                        }
                        return true
                    }

                    R.id.remove_bookmark -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            dao.delete(
                                Bookmark(
                                    naviArgs.id,
                                    naviArgs.title,
                                    naviArgs.url,
                                    date,
                                    date
                                )
                            )

                            CoroutineScope(Dispatchers.Main).launch {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(R.string.bookmark_delete_message)
                                    .setPositiveButton(R.string.ok) { _, _ ->
                                        getBookmark()
                                    }
                                    .show()
                            }
                        }
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)
    }

    /**
     * ブックマーク更新日時が１日以上経過していたら更新する
     */
    private fun updateBookmark() {
        bookmark?.let { bookmark ->
            if (ChronoUnit.SECONDS.between(
                    fromStringToDate(bookmark.updated_at),
                    now()
                ) >= 60 * 60 * 24
            ) {
                APIManager.get<Article>("items/${bookmark.id}", {
                    CoroutineScope(Dispatchers.Default).launch {
                        dao.update(Bookmark(it.id, it.title, it.url, bookmark.created_at, nowStr()))
                    }
                }) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("記事が見つかりませんでした。")
                        .setMessage("記事が削除された可能性があります。ブックマークを削除しますか?")
                        .setPositiveButton(R.string.ok) { _, _ ->
                            CoroutineScope(Dispatchers.Default).launch {
                                dao.delete(bookmark)
                            }

                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.bookmark_delete_message)
                                .setPositiveButton(R.string.ok) { _, _ ->
                                    view?.findNavController()
                                        ?.navigate(R.id.action_articleDetailFragment_to_bookmarkListFragment)
                                }
                                .show()

                        }
                        .setNegativeButton(R.string.cancel) { _, _ -> }
                        .show()
                }
            }
        }
    }


    private fun getBookmark() {
        CoroutineScope(Dispatchers.Default).launch {
            bookmark = dao.getBookmark(naviArgs.id)
        }
        addMenu()
    }


    private fun fromStringToDate(str: String): LocalDateTime {
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        return LocalDateTime.parse(str, dtf)
    }

    private fun now(): LocalDateTime {
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        return LocalDateTime.parse(LocalDateTime.now().format(dtf), dtf)
    }

    private fun nowStr(): String {
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        return LocalDateTime.now().format(dtf)
    }

}