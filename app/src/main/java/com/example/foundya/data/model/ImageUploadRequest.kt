package com.example.foundya.data.model

data class ImageUploadRequest(
    val userId: String,
    val fileBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageUploadRequest
        return userId == other.userId && fileBytes.contentEquals(other.fileBytes)
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + fileBytes.contentHashCode()
        return result
    }
}