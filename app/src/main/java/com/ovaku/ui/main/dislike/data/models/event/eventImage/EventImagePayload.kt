package com.ovaku.ui.main.dislike.data.models.event.eventImage

import com.ovaku.data.models.event.EventPayload

data class EventImagePayload(
    val event: EventPayload,
    val extension: String,
    val id: Int,
    val imageSelectionStatus: String,
    val imageType: String,
    val isActive: Boolean,
    val name: String,
    val size: Int,
    val url: String
)