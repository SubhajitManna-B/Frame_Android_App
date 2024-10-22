package com.ovaku.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovaku.data.dataSource.repository.AuthRepository
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserLoginPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _preferenceUserStatus = MutableLiveData<Resource<UserLoginPayload>>()
    val preferenceUserStatus: LiveData<Resource<UserLoginPayload>> = _preferenceUserStatus

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

    override fun onCleared() {
        super.onCleared()

    }
}