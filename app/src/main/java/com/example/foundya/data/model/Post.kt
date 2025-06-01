package com.example.foundya.data.model

import java.util.Date

data class Post(
    val id: String = "",
    val type: String, // "found" или "lost"
    val title: String,
    val description: String,
    val location: String,
    val contact: String,
    val timestamp: Date = Date()
)