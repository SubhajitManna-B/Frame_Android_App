package com.ovaku.ui.main.dislike.data.models.userLogin

data class UserLoginPayload(
    val accessToken: String,
    val refreshToken: String,
    val id: Int = 0
)