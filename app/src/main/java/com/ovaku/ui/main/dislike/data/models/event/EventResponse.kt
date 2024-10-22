package com.ovaku.ui.main.dislike.data.models.event

data class EventResponse(
    val message: String,
    val payload: MutableList<EventPayload>,
    val status: Int
)