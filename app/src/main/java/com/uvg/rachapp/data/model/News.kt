package com.uvg.rachapp.data.model

data class News(
    val id: String = "",
    val title: String = "",
    val shortDesc: String = "",
    val fullText: String = "",
    val order: Int = 0,
    val openCount: Int = 0,
    val imageResName: String = "" // news1, news2, etc.
)