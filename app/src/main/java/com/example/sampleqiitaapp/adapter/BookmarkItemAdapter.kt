package com.example.sampleqiitaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleqiitaapp.data.Bookmark
import com.example.sampleqiitaapp.R

class BookmarkItemAdapter(val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarkItemAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bookmarkView: LinearLayout
        var titleTextView: TextView

        init {
            bookmarkView = view.findViewById(R.id.bookmark_view)
            titleTextView = view.findViewById(R.id.title_textView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = bookmarks.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = bookmarks[position]

        holder.titleTextView.text = bookmark.title
        holder.bookmarkView.setOnClickListener {
            listener.onItemClickListener(it, position, bookmark.id, bookmark.url, bookmark.title)
        }

    }


    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int, id: String, url: String, title: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}