package com.ovaku.ui.main.dislike.data.models.event.eventImage

data class EventImageResponse(
    val message: String,
    val payload: MutableList<EventImagePayload>,
    val status: Int
)