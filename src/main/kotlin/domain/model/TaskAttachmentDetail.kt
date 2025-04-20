package domain.model

data class TaskAttachmentDetail(
    val id: Int,
    val fileKey: String,
    val fileName: String,
    val contentType: String,
    val downloadUrl: String,
)
