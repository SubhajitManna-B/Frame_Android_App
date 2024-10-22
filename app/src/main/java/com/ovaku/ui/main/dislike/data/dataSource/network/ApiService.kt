package com.ovaku.ui.main.dislike.data.dataSource.network

import com.ovaku.data.models.event.EventResponse
import com.ovaku.data.models.event.eventImage.EventImageResponse
import com.ovaku.data.models.event.eventImage.EventImageUpdate
import com.ovaku.data.models.profileDetails.ProfilePayload
import com.ovaku.data.models.profileDetails.ProfileResponse
import com.ovaku.data.models.userLogin.UserLoginResponse
import com.ovaku.data.models.userLogin.UserSendData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("auth/clients/login")
    suspend fun userLogin(
        @Body  userSendData: UserSendData
    ): UserLoginResponse

    @GET("clients/{userId}/events")
    suspend fun fetchAllEvent(
        @Path("userId") userId: Int,
        @Header("Authorization") basicToken: String
    ): EventResponse

    @GET("clients/{userId}/events/{eventId}/images")
    suspend fun fetchEventImages(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int,
        @Query("imageSelectionStatus") status: String,
        @Header("Authorization") basicToken: String
    ): EventImageResponse

    @GET("clients/{userId}/events/{eventId}/images")
    suspend fun fetchEventImagesHome(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int,
        @Header("Authorization") basicToken: String
    ): EventImageResponse

    @PUT("clients/{userId}/events/{eventId}/images/bulk-selection")
    suspend fun updateEventImages(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int,
        @Header("Authorization") basicToken: String,
        @Body  eventImageDataList: MutableList<EventImageUpdate>
    ): EventImageResponse

    @GET("clients/{userId}")
    suspend fun fetchProfile(
        @Path("userId") userId: Int,
        @Header("Authorization") basicToken: String
    ): ProfileResponse

    @PUT("clients/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: Int,
        @Header("Authorization") basicToken: String,
        @Body  profilePayload: ProfilePayload
    ): ProfileResponse

    @Multipart
    @PUT("clients/{userId}/upload")
    suspend fun updateProfileImage(
        @Part file: MultipartBody.Part,
        @Path("userId") userId: Int,
        @Header("Authorization") basicToken: String
    ): ProfileResponse
}