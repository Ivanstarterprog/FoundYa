package com.example.foundya.data.repository

import com.example.foundya.data.model.ApiResponse
import com.example.foundya.data.remote.api.ImageApi
import com.example.foundya.utils.BackendConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ImageRepositoryImpl @Inject constructor(
    private val api: ImageApi,
    private val okHttpClient: OkHttpClient
) : ImageRepository {

    override suspend fun uploadImage(file: File): ApiResponse {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaType())
            val filePart = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

            val response = api.uploadImage(filePart)
            ApiResponse(
                success = true,
                imageId = response.imageId,
                url = response.url
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = "Upload failed: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getImageUrl(imageId: Int): String? {
        return try {
            val response = api.getImageUrl(imageId)
            "${BackendConfig.BASE_URL.removeSuffix("/")}${response.url}"
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPlaceholderImageUrl(): String {
        return try {
            val response = api.getImageUrl(BackendConfig.PLACEHOLDER_IMAGE_ID)
            response.url
        } catch (e: Exception) {
            "${BackendConfig.BASE_URL.removeSuffix("/")}/api/uploads/default_placeholder.jpg"
        }
    }

    override suspend fun deleteImage(imageId: Int): Boolean {
        return try {
            val request = Request.Builder()
                .url("${BackendConfig.BASE_URL}api/images/$imageId")
                .delete()
                .build()

            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

interface ImageRepository {
    suspend fun uploadImage(file: File): ApiResponse
    suspend fun getImageUrl(imageId: Int): String?
    suspend fun getPlaceholderImageUrl(): String
    suspend fun deleteImage(imageId: Int): Boolean
}