package domain.model

data class TaskAttachmentUploadInfo(
    val id: Int,
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Int,
)
