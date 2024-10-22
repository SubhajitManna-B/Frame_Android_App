package com.ovaku.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ovaku.data.dataSource.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    val progressView = MutableLiveData<Boolean>(false)

    init {

    }

    override fun onCleared() {
        super.onCleared()

    }
}