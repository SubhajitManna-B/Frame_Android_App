package com.ovaku.ui.main.dislike.data.dataSource.repository

import android.content.SharedPreferences
import com.ovaku.data.dataSource.network.ApiService
import com.ovaku.data.dataSource.preferences.PreferenceHelper.clearPreference
import com.ovaku.data.models.profileDetails.ProfilePayload
import com.ovaku.data.models.profileDetails.ProfileResponse
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.utils.ext.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {

    /** User Login Through Api */
    suspend fun fetchProfile(userId: Int, authToken: String): Resource<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.fetchProfile(userId = userId, basicToken = "Bearer $authToken")
                Resource.Success(result, "Logged In Successfully")
            }
        }
    }

    suspend fun updateProfile(userId: Int, authToken: String, profilePayload: ProfilePayload): Resource<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.updateProfile(userId = userId, basicToken = "Bearer $authToken", profilePayload = profilePayload)
                Resource.Success(result, "Logged In Successfully")
            }
        }
    }

    suspend fun updateProfileImage(file: MultipartBody.Part,userId: Int, authToken: String): Resource<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.updateProfileImage(file = file, userId = userId, basicToken = "Bearer $authToken")
                Resource.Success(result, "Logged In Successfully")
            }
        }
    }
}