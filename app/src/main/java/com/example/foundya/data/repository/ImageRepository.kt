package com.example.foundya.data.repository

import com.example.foundya.data.model.ApiResponse
import com.example.foundya.data.remote.api.ImageApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageApi: ImageApi
) : ImageRepository {

    override suspend fun uploadImage(fileBytes: ByteArray): ApiResponse {
        return try {
            val tempFile = File.createTempFile("upload", ".jpg").apply {
                deleteOnExit()
                FileOutputStream(this).use { it.write(fileBytes) }
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaType())
            val filePart = MultipartBody.Part.createFormData(
                "file",
                tempFile.name,
                requestFile
            )

            val response = imageApi.uploadImage(filePart)

            ApiResponse(
                success = true,
                imageId = response.imageId,
                url = response.url
            )
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = "Error: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getImageUrl(imageId: Int): String? {
        return try {
            val response = imageApi.getImageUrl(imageId)
            response.url
        } catch (e: Exception) {
            null
        }
    }
}

interface ImageRepository {
    suspend fun uploadImage(fileBytes: ByteArray): ApiResponse
    suspend fun getImageUrl(imageId: Int): String?
}