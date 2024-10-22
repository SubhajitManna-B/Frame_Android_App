package com.ovaku.ui.main.dislike.data.dataSource.repository

import com.ovaku.data.dataSource.network.ApiService
import com.ovaku.data.models.event.EventResponse
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.utils.ext.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(private val apiService: ApiService) {

    /** User Login Through Api */
    suspend fun fetchAllEvent(userId: Int, authToken: String): Resource<EventResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.fetchAllEvent(userId = userId, basicToken = "Bearer $authToken")
                Resource.Success(result, "All Event Fetch Successfully")
            }
        }
    }

}