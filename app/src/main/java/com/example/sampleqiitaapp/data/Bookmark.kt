package com.example.sampleqiitaapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bookmark_table")
data class Bookmark(
    @PrimaryKey val id: String,
    @ColumnInfo val title: String,
    @ColumnInfo val url: String,
    @ColumnInfo val created_at: String,
    @ColumnInfo val updated_at: String
)