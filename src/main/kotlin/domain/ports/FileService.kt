package domain.ports

import domain.model.file.FileDownloadInfo
import domain.model.file.FileUploadInfo

interface FileService {
    suspend fun prepareFileUpload(fileName: String, contentType: String, fileSize: Long): FileUploadInfo
    suspend fun generateDownloadLink(fileKey: String): FileDownloadInfo
    suspend fun checkFileExists(fileKey: String): Boolean
}