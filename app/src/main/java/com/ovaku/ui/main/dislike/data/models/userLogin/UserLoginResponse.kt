package com.ovaku.ui.main.dislike.data.models.userLogin

data class UserLoginResponse(
    val message: String,
    val payload: UserLoginPayload,
    val status: Int
)