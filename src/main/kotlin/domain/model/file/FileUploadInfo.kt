package domain.model.file

data class FileUploadInfo(
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Int,
)