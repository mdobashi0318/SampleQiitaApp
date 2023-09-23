package com.example.sampleqiitaapp.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleqiitaapp.R
import com.example.sampleqiitaapp.adapter.BookmarkItemAdapter
import com.example.sampleqiitaapp.databinding.FragmentBookmarkListBinding
import com.example.sampleqiitaapp.viewmodels.BookmarkListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookmarkListFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkListBinding

    private val viewModel: BookmarkListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkListBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        addMenu()
        setItemAdapter()
        return binding.root
    }

    private fun setItemAdapter() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getBookmarkList()
            val adapter = BookmarkItemAdapter(viewModel.bookmarks.value ?: listOf())
            if (adapter.bookmarks.isEmpty()) {
                binding.noBookmarkItemView.noBookmarkView.visibility = View.VISIBLE
            }

            binding.recyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : BookmarkItemAdapter.OnItemClickListener {
                override fun onItemClickListener(
                    view: View,
                    position: Int,
                    id: String,
                    url: String,
                    title: String
                ) {
                    view.findNavController().navigate(
                        BookmarkListFragmentDirections.actionBookmarkListFragmentToArticleDetailFragment(
                            id,
                            url,
                            title
                        )
                    )
                }
            })
        }

    }


    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.bookmark_list_menu, menu)
            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.delete_bookmark -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.bookmark_deleteAll_confirm_delete_message)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                viewModel.deleteAllBookmark ({
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle(R.string.bookmark_delete_message)
                                        .setPositiveButton(R.string.ok) { _, _ ->
                                            setItemAdapter()
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
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .setCancelable(false)
                            .show()

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}