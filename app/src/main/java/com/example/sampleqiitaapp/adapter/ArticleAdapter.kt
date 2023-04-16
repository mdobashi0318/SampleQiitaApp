package com.example.sampleqiitaapp.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleqiitaapp.R
import com.example.sampleqiitaapp.data.Article
import java.util.*

class ArticleAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    lateinit var listener: OnItemClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var articleView: LinearLayout
        var userIdTextView: TextView
        var userNameTextView: TextView
        val organizationTextView: TextView
        var createdTextView: TextView
        var titleTextView: TextView
        var tagsTextView: TextView
        var likeCountTextView: TextView

        init {
            articleView = view.findViewById(R.id.article_view)
            userIdTextView = view.findViewById(R.id.user_id_textView)
            userNameTextView = view.findViewById(R.id.user_name_textView)
            organizationTextView = view.findViewById(R.id.organization_textView)
            createdTextView = view.findViewById(R.id.created_at_textView)
            titleTextView = view.findViewById(R.id.title_textView)
            tagsTextView = view.findViewById(R.id.tags_textView)
            likeCountTextView = view.findViewById(R.id.like_count_textView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = articles.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.userIdTextView.text = "@${article.user.id}"

        holder.userNameTextView.visibility = View.GONE
        if (article.user.name.isNotEmpty()) {
            holder.userNameTextView.visibility = View.VISIBLE
            holder.userNameTextView.text = "(${article.user.name})"
        }

        holder.organizationTextView.visibility = View.GONE

        article.user.organization?.let {
            holder.organizationTextView.visibility = View.VISIBLE
            holder.organizationTextView.text = it
        }

        holder.createdTextView.text = stringToStringDateFormat(article.created_at)

        holder.titleTextView.text = article.title

        holder.tagsTextView.text = ""

        article.tags.forEach {
            if (holder.tagsTextView.text.isEmpty()) {
                holder.tagsTextView.text = it.name
            } else {
                holder.tagsTextView.text = "${holder.tagsTextView.text}, ${it.name}"
            }
        }

        holder.likeCountTextView.text = article.likes_count.toString()

        holder.articleView.setOnClickListener {
            listener.onItemClickListener(it, position, article.id, article.url, article.title)
        }

    }

    /**
    文字列の日付をdateに戻してから再度フォーマットを変更して文字列に戻す
     **/
    @SuppressLint("SimpleDateFormat")
    private fun stringToStringDateFormat(str: String): String {
        val df1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
        val dt = df1.parse(str)


        val df2 = SimpleDateFormat("yyyy年MM月dd日")
        return df2.format(dt)
    }


    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int, id: String, url: String, title: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}