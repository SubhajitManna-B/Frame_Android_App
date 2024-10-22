package com.ovaku.ui.main.dislike.data.models.profileDetails

data class ProfilePayload(
    val address: ProfileAddress,
    val email: String,
    val firstName: String,
    val id: Int,
    val isActive: Boolean,
    val lastName: String,
    val name: String,
    val password: String,
    val phoneNo: Long,
    val profileImageUrl: String
)