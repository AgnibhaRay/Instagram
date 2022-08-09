package com.example.compose.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Post(
    val ID: String = "",
    val Caption: String = "",
    val Media_Type: String = "",
    val Media_URl: String = "",
    val UsersLiked: List<String> = emptyList(),
    val comments: List<Comment> = emptyList(),
    @ServerTimestamp val Time: Timestamp? = null
)

data class Comment(
    val uid: String = "",
    val comment: String = "",
    val time: Int = 0
)