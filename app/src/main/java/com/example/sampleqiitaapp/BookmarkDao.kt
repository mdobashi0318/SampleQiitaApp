package com.example.sampleqiitaapp

import androidx.room.*
import com.example.sampleqiitaapp.data.Bookmark


@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmark_table")
    suspend fun getAll(): List<Bookmark>

    @Query("SELECT * FROM bookmark_table WHERE id == :id")
    suspend fun getBookmark(id: String): Bookmark?

    @Insert
    suspend fun add(bm: Bookmark)

    @Update
    suspend fun update(bm: Bookmark)

    @Delete
    fun delete(bm: Bookmark)

    @Query("Delete FROM bookmark_table")
    fun deleteAll()
}