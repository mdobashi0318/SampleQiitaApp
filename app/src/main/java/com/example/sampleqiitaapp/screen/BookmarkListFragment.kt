package com.example.sampleqiitaapp.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleqiitaapp.adapter.BookmarkItemAdapter
import com.example.sampleqiitaapp.databinding.FragmentBookmarkListBinding
import com.example.sampleqiitaapp.viewmodels.BookmarkListViewModel
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

}