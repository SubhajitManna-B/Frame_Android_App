package com.ovaku.ui.main.dislike.data.dataSource.repository

import android.content.SharedPreferences
import com.ovaku.data.dataSource.network.ApiService
import com.ovaku.data.dataSource.preferences.PreferenceHelper
import com.ovaku.data.dataSource.preferences.PreferenceHelper.ACCESS_TOKEN
import com.ovaku.data.dataSource.preferences.PreferenceHelper.ID
import com.ovaku.data.dataSource.preferences.PreferenceHelper.REFRESH_TOKEN
import com.ovaku.data.dataSource.preferences.PreferenceHelper.clearPreference
import com.ovaku.data.dataSource.preferences.PreferenceHelper.get
import com.ovaku.data.dataSource.preferences.PreferenceHelper.set
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserDetails
import com.ovaku.data.models.userLogin.UserLoginPayload
import com.ovaku.data.models.userLogin.UserLoginResponse
import com.ovaku.data.models.userLogin.UserSendData
import com.ovaku.utils.ext.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService, private val preferences: SharedPreferences) {

    /** User Login Through Api */
    suspend fun userLogin(userSendData: UserSendData): Resource<UserLoginResponse> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = apiService.userLogin(userSendData)
                Resource.Success(result, "Logged In Successfully")
            }
        }
    }

    /** Store User Details in Shared Preference
     * @param userLoginPayload
     * @param userDetails*/
    suspend fun storeUserAuthDetails(userLoginPayload: UserLoginPayload, userDetails: UserDetails): Resource<String> {
        return withContext(Dispatchers.IO) {
            safeCall {
                with(preferences) {
                    set(ACCESS_TOKEN, userLoginPayload.accessToken)
                    set(REFRESH_TOKEN, userLoginPayload.refreshToken)
                    set(ID, userDetails.id)
                }
                Resource.Success(null, "Data Stored!")
            }
        }
    }

    /** Fetch User Details from Shared Preference
     * @return userDetails */
    suspend fun fetchPreferenceUserDetails(): Resource<UserLoginPayload> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val userData = with(preferences) {
                    UserLoginPayload(
                        accessToken = get(ACCESS_TOKEN)?: "",
                        refreshToken = get(REFRESH_TOKEN)?: "",
                        id = get(ID)?:0
                    )
                }
                Resource.Success(userData, "Data Fetched!")
            }
        }
    }

    suspend fun logout(): Resource<String>{
        return withContext(Dispatchers.IO){
            safeCall {
                preferences.clearPreference()
                Resource.Success(null, "Logout successfully!")
            }
        }
    }
}