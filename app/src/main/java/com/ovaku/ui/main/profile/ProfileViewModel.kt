package com.ovaku.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovaku.data.dataSource.repository.AuthRepository
import com.ovaku.data.dataSource.repository.EventImageRepository
import com.ovaku.data.dataSource.repository.EventRepository
import com.ovaku.data.dataSource.repository.UserRepository
import com.ovaku.data.models.event.EventResponse
import com.ovaku.data.models.event.eventImage.EventImageResponse
import com.ovaku.data.models.event.eventImage.EventImageUpdate
import com.ovaku.data.models.profileDetails.ProfilePayload
import com.ovaku.data.models.profileDetails.ProfileResponse
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserLoginPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _fetchProfileStatus = MutableLiveData<Resource<ProfileResponse>>()
    val fetchProfileStatus: LiveData<Resource<ProfileResponse>> = _fetchProfileStatus

    private val _updateProfileStatus = MutableLiveData<Resource<ProfileResponse>>()
    val updateProfileStatus: LiveData<Resource<ProfileResponse>> = _updateProfileStatus

    private val _updateProfileImageStatus = MutableLiveData<Resource<ProfileResponse>>()
    val updateProfileImageStatus: LiveData<Resource<ProfileResponse>> = _updateProfileImageStatus

    init {

    }

    /** Fetch event Images */
    fun fetchProfile(userId: Int, authToken: String) {
        _fetchProfileStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = userRepository.fetchProfile(userId = userId, authToken = authToken)
            _fetchProfileStatus.postValue(userDataResult)
        }
    }

    fun updateProfile(userId: Int, authToken: String, profilePayload: ProfilePayload) {
        _updateProfileStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = userRepository.updateProfile(userId = userId, authToken = authToken, profilePayload = profilePayload)
            _updateProfileStatus.postValue(userDataResult)
        }
    }

    fun updateProfileImage(file: MultipartBody.Part, userId: Int, authToken: String) {
        _updateProfileImageStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDataResult = userRepository.updateProfileImage(file = file, userId = userId, authToken = authToken)
            _updateProfileImageStatus.postValue(userDataResult)
        }
    }

    override fun onCleared() {
        super.onCleared()

    }
}