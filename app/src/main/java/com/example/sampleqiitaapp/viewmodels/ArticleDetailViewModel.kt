package com.example.sampleqiitaapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sampleqiitaapp.APIManager
import com.example.sampleqiitaapp.QiitaApplication
import com.example.sampleqiitaapp.data.Article
import com.example.sampleqiitaapp.data.Bookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ArticleDetailViewModel : ViewModel() {

    private val dao = QiitaApplication.database.bookmarkDao()

    private val _bookmark = MutableLiveData<Bookmark>()
    val bookmark: LiveData<Bookmark>
        get() = _bookmark

    private lateinit var id: String
    private lateinit var title: String
    private lateinit var _url: String
    val url: String
        get() = _url


    fun setArgs(id: String, title: String, url: String) {
        this.id = id
        this.title = title
        this._url = url
    }


    suspend fun get() {
        _bookmark.value = dao.getBookmark(id)
    }

    fun add() {
        val date = nowStr()
        CoroutineScope(Dispatchers.Default).launch {
            dao.add(
                Bookmark(
                    id,
                    title,
                    url,
                    date,
                    date
                )

            )
        }
    }

    /**
     * ブックマーク更新日時が1時間以上経過していたら更新する
     */
    fun update(
        failure: () -> Unit
    ) {
        _bookmark.value?.let { bookmark ->
            if (ChronoUnit.SECONDS.between(
                    fromStringToDate(bookmark.updated_at),
                    now()
                ) >= 60 * 60
            ) {
                APIManager.get<Article>("items/${bookmark.id}", {
                    CoroutineScope(Dispatchers.Default).launch {
                        dao.update(Bookmark(it.id, it.title, it.url, bookmark.created_at, nowStr()))
                    }
                }) {
                    failure()
                }

            }
        }
    }


    fun delete() {
        CoroutineScope(Dispatchers.Default).launch {
            dao.delete(
                Bookmark(
                    id,
                    title,
                    url,
                    "",
                    ""
                )
            )
        }
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