package com.example.sampleqiitaapp.screen

import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sampleqiitaapp.ErrorType
import com.example.sampleqiitaapp.R
import com.example.sampleqiitaapp.databinding.FragmentArticleDetailBinding
import com.example.sampleqiitaapp.viewmodels.ArticleDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArticleDetailFragment : Fragment() {

    private lateinit var binding: FragmentArticleDetailBinding

    private val naviArgs: ArticleDetailFragmentArgs by navArgs()

    private val viewModel: ArticleDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setArgs(naviArgs.id, naviArgs.title, naviArgs.url)
        binding = FragmentArticleDetailBinding.inflate(layoutInflater, container, false)
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(viewModel.url)
        binding.webView.setOnKeyListener { _, keyCode, event ->
            (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && binding.webView.canGoBack()).apply {
                binding.webView.goBack()
            }
        }
        getBookmark()
        return binding.root
    }


    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
                if (viewModel.bookmark.value == null) {
                    menu.getItem(BookmarkMenuItem.Add.value).isVisible = true
                    menu.getItem(BookmarkMenuItem.Remove.value).isVisible = false
                } else {
                    menu.getItem(BookmarkMenuItem.Add.value).isVisible = false
                    menu.getItem(BookmarkMenuItem.Remove.value).isVisible = true
                }
            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.add_bookmark -> {
                        viewModel.add({
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("ブックマークに追加しました。")
                                .setPositiveButton(R.string.ok) { _, _ ->
                                    getBookmark()
                                }
                                .setCancelable(false)
                                .show()
                        }) {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("ブックマークの追加に失敗しました。")
                                .setPositiveButton(R.string.ok) { _, _ -> }
                                .setCancelable(false)
                                .show()
                        }

                        return true
                    }

                    R.id.remove_bookmark -> {
                        deleteBookmark()

                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun deleteBookmark() {
        viewModel.delete({
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.bookmark_delete_message)
                .setPositiveButton(R.string.ok) { _, _ ->
                    getBookmark()
                }
                .setCancelable(false)
                .show()
        }) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("ブックマークの削除に失敗しました。")
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setCancelable(false)
                .show()
        }

    }


    /**
     * ブックマークを更新する
     */
    private fun updateBookmark() {
        viewModel.update {
            if (it == ErrorType.API) {
                apiErrorDialog()
            } else {
                dbErrorDialog()
            }
        }
    }


    private fun getBookmark() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.get()
            if (viewModel.bookmark.value != null) updateBookmark()
            addMenu()
        }
    }

    private enum class BookmarkMenuItem(val value: Int) {
        Add(0),
        Remove(1)
    }


    private val apiErrorDialog = {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("記事が見つかりませんでした。")
            .setMessage("記事が削除された可能性があります。ブックマークを削除しますか?")
            .setPositiveButton(R.string.ok) { _, _ ->
                deleteBookmark()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.bookmark_delete_message)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        view?.findNavController()?.popBackStack()
                    }
                    .setCancelable(false)
                    .show()

            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .setCancelable(false)
            .show()
    }

    private val dbErrorDialog = {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("更新に失敗しました")
            .setPositiveButton(R.string.ok) { _, _ -> }
            .setCancelable(false)
            .show()
    }
}