package com.example.sampleqiitaapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sampleqiitaapp.QiitaApplication
import com.example.sampleqiitaapp.data.Bookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkListViewModel : ViewModel() {

    private val _bookmarks = MutableLiveData<List<Bookmark>>()

    private val dao = QiitaApplication.database.bookmarkDao()

    val bookmarks: LiveData<List<Bookmark>>
        get() = _bookmarks

    suspend fun getBookmarkList() {
        _bookmarks.value = dao.getAll()
    }


    fun deleteAllBookmark(success: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAll()
            CoroutineScope(Dispatchers.Main).launch {
                success()
            }
        }
    }
}