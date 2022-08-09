package com.example.compose.entity

data class User(
    val username: String = "",
    val displayName: String = "",
    val image: String = "",
    val email: String = "",
    val biography: String = "",
    val edge_followers: List<String> = listOf(),
    var edge_following: List<String> = listOf(),
    val edge_posts: List<String> = listOf(),
    @field:JvmField val is_private: Boolean = false,
    @field:JvmField val is_verified: Boolean = false,
)