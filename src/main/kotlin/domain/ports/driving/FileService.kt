package domain.ports.driving

import domain.model.file.FileDownloadInfo
import domain.model.file.FileUploadInfo

interface FileService {
    suspend fun prepareFileUpload(fileName: String, contentType: String, fileSize: Int): FileUploadInfo

    suspend fun generateDownloadLink(fileKey: String): FileDownloadInfo

    suspend fun checkFileExists(fileKey: String): Boolean

    suspend fun deleteFile(fileKey: String): Boolean
}
