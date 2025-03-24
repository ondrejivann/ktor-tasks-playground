package domain.model.file

data class FileDownloadInfo(
    val downloadUrl: String,
    val fileName: String,
    val expiresInSeconds: Long
)