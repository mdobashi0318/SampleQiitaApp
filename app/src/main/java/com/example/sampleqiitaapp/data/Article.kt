package com.example.sampleqiitaapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val created_at: String,
    val likes_count: Int,
    val title: String,
    val user: User,
    val tags: List<Tags>,
    val url: String,
    val id: String,
)


