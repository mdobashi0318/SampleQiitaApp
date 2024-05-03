package com.example.sampleqiitaapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sampleqiitaapp.APIManager
import com.example.sampleqiitaapp.data.Article
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ArticleListViewModel : ViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>>
        get() = _articles

    private var date: LocalDateTime? = null

    var searchStr = ""

    /**
    記事一覧を取得するか判定する
     **/
    fun getArticleFlag(): Boolean {
        return if (date == null || ChronoUnit.MINUTES.between(date, LocalDateTime.now()) >= 5) {
            date = LocalDateTime.now()
            true
        } else {
            false
        }
    }
    /**
    記事一覧を取得する
     **/
    fun getArticle(
        success: () -> Unit,
        failure: () -> Unit
    ) {
        APIManager.get<List<Article>>("items", searchStr, {
            _articles.value = it
            success()
        }) {
            failure()
        }
    }
}