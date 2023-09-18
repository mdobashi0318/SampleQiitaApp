package com.example.sampleqiitaapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sampleqiitaapp.QiitaApplication
import com.example.sampleqiitaapp.data.Bookmark

class BookmarkListViewModel : ViewModel() {

    private val _bookmarks = MutableLiveData<List<Bookmark>>()
    val bookmarks: LiveData<List<Bookmark>>
        get() = _bookmarks

    suspend fun getBookmarkList() {
        _bookmarks.value = QiitaApplication.database.bookmarkDao().getAll()
    }
}