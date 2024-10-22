package com.ovaku.ui.main.dislike.data.dataSource.repository

import com.ovaku.data.dataSource.network.ApiService
import com.ovaku.data.models.event.EventResponse
import com.ovaku.data.models.event.eventImage.EventImageResponse
import com.ovaku.data.models.event.eventImage.EventImageUpdate
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.utils.ext.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import javax.inject.Inject

class EventImageRepository @Inject constructor(private val apiService: ApiService) {

    /** User Login Through Api */
    suspend fun fetchEventImages(userId: Int,eventId: Int, status: String,authToken: String): Resource<EventImageResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.fetchEventImages(userId = userId, eventId = eventId, status = status, basicToken = "Bearer $authToken")
                Resource.Success(result, "Event Images Fetch Successfully")
            }
        }
    }

    suspend fun fetchEventImagesHome(userId: Int,eventId: Int, authToken: String): Resource<EventImageResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.fetchEventImagesHome(userId = userId, eventId = eventId, basicToken = "Bearer $authToken")
                Resource.Success(result, "Event Images Fetch Successfully")
            }
        }
    }

    suspend fun updateEventImages(userId: Int,eventId: Int, authToken: String, eventImageDataList: MutableList<EventImageUpdate>): Resource<EventImageResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.updateEventImages(userId = userId, eventId = eventId, basicToken = "Bearer $authToken", eventImageDataList = eventImageDataList )
                Resource.Success(result, "Event Images Update Successfully")
            }
        }
    }

}