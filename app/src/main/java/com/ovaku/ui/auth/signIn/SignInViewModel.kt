package com.ovaku.ui.auth.signIn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovaku.data.dataSource.repository.AuthRepository
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserDetails
import com.ovaku.data.models.userLogin.UserLoginPayload
import com.ovaku.data.models.userLogin.UserLoginResponse
import com.ovaku.data.models.userLogin.UserSendData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _userLoginStatus = MutableLiveData<Resource<UserLoginResponse>>()
    val userLoginStatus: LiveData<Resource<UserLoginResponse>> = _userLoginStatus

    private val _preferenceStoreUserStatus = MutableLiveData<Resource<String>>()
    val preferenceStoreUserStatus: LiveData<Resource<String>> = _preferenceStoreUserStatus

    fun userLogin(userSendData: UserSendData) {
        _userLoginStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDetailsResult = authRepository.userLogin(userSendData)
            _userLoginStatus.postValue(userDetailsResult)
        }
    }

    /** Store User Details in Shared Preference
     * @param userDetails */
    fun storeUserDetails(userLoginPayload: UserLoginPayload, userDetails: UserDetails) {
        _preferenceStoreUserStatus.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.Main) {
            val userDetailsResult = authRepository.storeUserAuthDetails(userLoginPayload,userDetails)
            _preferenceStoreUserStatus.postValue(userDetailsResult)
        }
    }
}