package infrastructure.rest.dto

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FileUploadRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Int
)

@Serializable
data class FileUploadResponse(
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Int
)

@Serializable
data class FileDownloadRequest(
    val fileKey: String
)

@Serializable
data class FileDownloadResponse(
    val downloadUrl: String,
    val fileName: String,
    val expiresInSeconds: Int
)

@Serializable
data class FileDeleteRequest(
    val fileKey: String,
)

@Serializable
data class ErrorResponse(
    val message: String,
    val code: String = "GENERAL_ERROR",
    val traceId: String = UUID.randomUUID().toString()
)