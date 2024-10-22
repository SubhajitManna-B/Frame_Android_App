package com.ovaku.ui.main.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovaku.data.dataSource.repository.AuthRepository
import com.ovaku.data.dataSource.repository.EventImageRepository
import com.ovaku.data.dataSource.repository.EventRepository
import com.ovaku.data.models.event.EventResponse
import com.ovaku.data.models.event.eventImage.EventImageResponse
import com.ovaku.data.models.event.eventImage.EventImageUpdate
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserLoginPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val eventImageRepository: EventImageRepository
) : ViewModel() {

    private val _fetchEventImageStatus = MutableLiveData<Resource<EventImageResponse>>()
    val fetchEventImageStatus: LiveData<Resource<EventImageResponse>> = _fetchEventImageStatus

    private val _updateEventImageStatus = MutableLiveData<Resource<EventImageResponse>>()
    val updateEventImageStatus: LiveData<Resource<EventImageResponse>> = _updateEventImageStatus

    init {

    }

    /** Fetch event Images */
    fun fetchEventImages(userId: Int, eventId :Int, authToken: String) {
        _fetchEventImageStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = eventImageRepository.fetchEventImages(userId = userId, eventId = eventId, status = "LOVE", authToken = authToken)
            _fetchEventImageStatus.postValue(userDataResult)
        }
    }

    /**Update event Images */
    fun updateEventImages(userId: Int, eventId :Int, authToken: String, eventImageDataList: MutableList<EventImageUpdate>) {
        _updateEventImageStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = eventImageRepository.updateEventImages(userId = userId, eventId = eventId, authToken = authToken, eventImageDataList = eventImageDataList)
            _updateEventImageStatus.postValue(userDataResult)
        }
    }

    override fun onCleared() {
        super.onCleared()

    }
}