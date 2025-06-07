package com.example.foundya.data.model

data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val imageId: Int? = null,
    val url: String? = null
) {
    companion object {
        fun error(exception: Exception) = ApiResponse(
            success = false,
            message = exception.localizedMessage
        )
    }
}