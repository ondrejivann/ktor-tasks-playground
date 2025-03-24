package infrastructure.rest.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Long
)

@Serializable
data class FileUploadResponse(
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Long
)

@Serializable
data class FileDownloadRequest(
    val fileKey: String
)

@Serializable
data class FileDownloadResponse(
    val downloadUrl: String,
    val fileName: String,
    val expiresInSeconds: Long
)

@Serializable
data class ErrorResponse(
    val message: String,
    val code: String = "GENERAL_ERROR"
)