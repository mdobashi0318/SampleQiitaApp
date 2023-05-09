package com.example.sampleqiitaapp.screen

import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.sampleqiitaapp.data.Bookmark
import com.example.sampleqiitaapp.QiitaApplication
import com.example.sampleqiitaapp.R
import com.example.sampleqiitaapp.databinding.FragmentArticleDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArticleDetailFragment : Fragment() {

    private lateinit var binding: FragmentArticleDetailBinding

    private val naviArgs: ArticleDetailFragmentArgs by navArgs()

    private val dao = QiitaApplication.database.bookmarkDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentArticleDetailBinding.inflate(layoutInflater, container, false)
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(naviArgs.url)
        addMenu()
        return binding.root
    }


    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
                CoroutineScope(Dispatchers.Default).launch {
                    val bookmark = dao.getBookmark(naviArgs.url)
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

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.add_bookmark -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            dao.add(
                                Bookmark(
                                    naviArgs.id,
                                    naviArgs.title,
                                    naviArgs.url
                                )
                            )
                            CoroutineScope(Dispatchers.Main).launch {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("ブックマークに追加しました。")
                                    .setPositiveButton(R.string.ok) { _, _ ->
                                        addMenu()
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
                                    naviArgs.url
                                )
                            )
                            CoroutineScope(Dispatchers.Main).launch {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("ブックマークから削除しました。")
                                    .setPositiveButton(R.string.ok) { _, _ ->
                                        addMenu()
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
}