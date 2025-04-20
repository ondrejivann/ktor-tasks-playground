package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskAttachment(
    val id: Int = -1,
    val taskId: Int,
    val fileKey: String,
    val fileName: String,
    val contentType: String,
    val uploadStatus: UploadStatus,
)
