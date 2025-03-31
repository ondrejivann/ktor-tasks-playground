package infrastructure.rest.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskAttachmentResponse(
    val id: Int,
    val fileName: String,
    val contentType: String,
    val downloadUrl: String,
)

@Serializable
data class PrepareTaskAttachmentUploadRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Int
)

@Serializable
data class PrepareTaskAttachmentUploadResponse(
    val id: Int,
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Int,
)

@Serializable
data class ConfirmTaskAttachmentUploadRequest(
    val fileKey: String
)

@Serializable
data class TaskAttachmentDownloadResponse(
    val downloadUrl: String
)