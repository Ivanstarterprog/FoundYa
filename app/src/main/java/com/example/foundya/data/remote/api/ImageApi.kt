package com.example.foundya.data.remote.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageApi {
    @Multipart
    @POST("api/images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): ImageUploadResponse

    @GET("api/images/{image_id}")
    suspend fun getImageUrl(@Path("image_id") imageId: Int): ImageUrlResponse
}

data class ImageUploadResponse(
    @SerializedName("image_id") val imageId: Int,
    @SerializedName("url") val url: String
)

data class ImageUrlResponse(
    @SerializedName("url") val url: String
)