package infrastructure.graphql.mutations

import application.exceptions.FileValidationException
import application.exceptions.ResourceNotFoundException
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
        return try {
            val fileUploadInfo = fileService.prepareFileUpload(
                fileName = input.fileName,
                contentType = input.contentType,
                fileSize = input.fileSize
            )

            FileUploadResultGQL(
                success = true,
                uploadUrl = fileUploadInfo.uploadUrl,
                fileKey = fileUploadInfo.fileKey,
                expiresInSeconds = fileUploadInfo.expiresInSeconds
            )
        } catch (e: FileValidationException) {
            FileUploadResultGQL(
                success = false,
                message = e.message
            )
        } catch (e: Exception) {
            FileUploadResultGQL(
                success = false,
                message = "Error preparing file upload: ${e.message}"
            )
        }
    }

    suspend fun generateDownloadLink(fileKey: String): FileDownloadResultGQL {
        return try {
            val downloadInfo = fileService.generateDownloadLink(fileKey)

            FileDownloadResultGQL(
                success = true,
                downloadUrl = downloadInfo.downloadUrl,
                fileName = downloadInfo.fileName,
                expiresInSeconds = downloadInfo.expiresInSeconds
            )
        } catch (e: ResourceNotFoundException) {
            FileDownloadResultGQL(
                success = false,
                message = "File not found: ${e.message}"
            )
        } catch (e: Exception) {
            FileDownloadResultGQL(
                success = false,
                message = "Error generating download link: ${e.message}"
            )
        }
    }

    suspend fun deleteFile(fileKey: String): FileOperationResultGQL {
        return try {
            val success = fileService.deleteFile(fileKey)

            if (success) {
                FileOperationResultGQL(
                    success = true,
                    message = "File successfully deleted"
                )
            } else {
                FileOperationResultGQL(
                    success = false,
                    message = "Failed to delete file"
                )
            }
        } catch (e: Exception) {
            FileOperationResultGQL(
                success = false,
                message = "Error deleting file: ${e.message}"
            )
        }
    }
}