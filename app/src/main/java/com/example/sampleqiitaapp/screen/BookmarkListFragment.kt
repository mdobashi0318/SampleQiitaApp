package com.example.sampleqiitaapp.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleqiitaapp.QiitaApplication
import com.example.sampleqiitaapp.adapter.BookmarkItemAdapter
import com.example.sampleqiitaapp.databinding.FragmentBookmarkListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookmarkListFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkListBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)

        read()
        return binding.root
    }

    private fun read() {

        CoroutineScope(Dispatchers.Default).launch {
            val list = QiitaApplication.database.bookmarkDao().getAll()

            CoroutineScope(Dispatchers.Main).launch {
                val adapter = BookmarkItemAdapter(list)
                binding.recyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : BookmarkItemAdapter.OnItemClickListener {
                    override fun onItemClickListener(
                        view: View,
                        position: Int,
                        url: String,
                        title: String
                    ) {
                        view.findNavController().navigate(
                            BookmarkListFragmentDirections.actionBookmarkListFragmentToArticleDetailFragment(
                                url,
                                title
                            )
                        )
                    }
                })
            }
        }

    }

}