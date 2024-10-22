package com.ovaku.ui.main.dislike.data.models.userLogin

data class UserDetails(
    val auth: UserAuth,
    val businessId: Int,
    val email: String,
    val exp: Int,
    val firstName: String,
    val iat: Int,
    val id: Int,
    val lastEventId: Int,
    val lastName: String,
    val phoneNo: Long
)