package infrastructure.graphql.mutations

import domain.ports.driving.FileService
import infrastructure.graphql.model.files.FileDownloadResultGQL
import infrastructure.graphql.model.files.FileOperationResultGQL
import infrastructure.graphql.model.files.FileUploadInputGQL
import infrastructure.graphql.model.files.FileUploadResultGQL
import org.koin.core.annotation.Single

@Single
class FileStorageMutations(
    private val fileService: FileService
) {
    suspend fun prepareFileUpload(input: FileUploadInputGQL): FileUploadResultGQL {
        val fileUploadInfo = fileService.prepareFileUpload(
            fileName = input.fileName,
            contentType = input.contentType,
            fileSize = input.fileSize
        )

        return FileUploadResultGQL(
            success = true,
            uploadUrl = fileUploadInfo.uploadUrl,
            fileKey = fileUploadInfo.fileKey,
            expiresInSeconds = fileUploadInfo.expiresInSeconds
        )
    }

    suspend fun generateDownloadLink(fileKey: String): FileDownloadResultGQL {
        val downloadInfo = fileService.generateDownloadLink(fileKey)

        return FileDownloadResultGQL(
            success = true,
            downloadUrl = downloadInfo.downloadUrl,
            fileName = downloadInfo.fileName,
            expiresInSeconds = downloadInfo.expiresInSeconds
        )
    }

    suspend fun deleteFile(fileKey: String): FileOperationResultGQL {
        val success = fileService.deleteFile(fileKey)
        return FileOperationResultGQL(
            success = true,
            message = "File successfully deleted"
        )
    }
}