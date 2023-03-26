package com.example.sampleqiitaapp.data

import kotlinx.serialization.Serializable


@Serializable
data class Tags(
    val name: String,
    val versions: List<String>
)
