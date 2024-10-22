package com.ovaku.ui.main.dislike.data.models.event

data class EventPayload(
    val date: String,
    val description: String,
    val id: Int,
    val isActive: Boolean,
    val name: String,
    val sourceDirectoryPath: String,
    var isSelected: Boolean = false
)