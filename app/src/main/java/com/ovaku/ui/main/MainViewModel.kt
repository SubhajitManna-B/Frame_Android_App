package com.ovaku.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovaku.data.dataSource.repository.AuthRepository
import com.ovaku.data.dataSource.repository.EventRepository
import com.ovaku.data.models.event.EventResponse
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserLoginPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    val progressView = MutableLiveData<Boolean>(false)
    val eventId = MutableLiveData<Int>(-1)
    val imageChoose = MutableLiveData<Int>(-1)

    private val _preferenceUserStatus = MutableLiveData<Resource<UserLoginPayload>>()
    val preferenceUserStatus: LiveData<Resource<UserLoginPayload>> = _preferenceUserStatus

    private val _fetchAllEventStatus = MutableLiveData<Resource<EventResponse>>()
    val fetchAllEventStatus: LiveData<Resource<EventResponse>> = _fetchAllEventStatus

    private val _logoutStatus = MutableLiveData<Resource<String>>()
    val logoutStatus: LiveData<Resource<String>> = _logoutStatus

    init {

    }

    /** Fetch User Type in Shared Preference */
    fun fetchPreferenceUserDetails() {
        _preferenceUserStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = authRepository.fetchPreferenceUserDetails()
            _preferenceUserStatus.postValue(userDataResult)
        }
    }

    /** Fetch all event */
    fun fetchAllEvent(userId: Int, authToken: String) {
        _fetchAllEventStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = eventRepository.fetchAllEvent(userId = userId,authToken = authToken)
            _fetchAllEventStatus.postValue(userDataResult)
        }
    }

    fun logout() {
        _logoutStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val logoutResult = authRepository.logout()
            _logoutStatus.postValue(logoutResult)
        }
    }

    override fun onCleared() {
        super.onCleared()

    }
}