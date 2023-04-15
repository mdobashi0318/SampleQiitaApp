package com.example.sampleqiitaapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bookmark_table")
data class Bookmark (
    @ColumnInfo val title: String,
    @PrimaryKey val url: String,
)