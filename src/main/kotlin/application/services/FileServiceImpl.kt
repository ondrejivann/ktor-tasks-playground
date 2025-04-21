package application.services

import application.exceptions.FileValidationException
import application.exceptions.ResourceNotFoundException
import common.exceptions.ErrorCodes
import domain.model.file.FileDownloadInfo
import domain.model.file.FileUploadInfo
import domain.ports.driven.FileStoragePort
import domain.ports.driving.FileService
import infrastructure.aws.s3.S3Config
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.config.ApplicationConfig
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class FileServiceImpl(private val fileStorage: FileStoragePort, config: ApplicationConfig) : FileService {
    private val logger = KotlinLogging.logger {}

    private val urlExpirationSeconds =
        config
            .property(S3Config.AWS_URL_EXPIRATION_SECONDS_PATH)
            .getString()
            .toInt()

    override suspend fun prepareFileUpload(fileName: String, contentType: String, fileSize: Int): FileUploadInfo {
        logger.debug { "Preparing upload for file: $fileName, type: $contentType, size: $fileSize" }

        validateFileSize(fileSize)
        validateContentType(contentType)

        val fileExtension = fileName.substringAfterLast('.', "")
        val fileKey =
            "${S3Config.UPLOADS_PREFIX}${UUID.randomUUID()}${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"

        val metadata = emptyMap<String, String>()

        val uploadUrl = fileStorage.generateUploadUrl(fileKey, contentType, metadata)

        return FileUploadInfo(
            uploadUrl = uploadUrl,
            fileKey = fileKey,
            expiresInSeconds = urlExpirationSeconds,
        )
    }

    override suspend fun generateDownloadLink(fileKey: String): FileDownloadInfo {
        logger.debug { "Generating download link for file: $fileKey" }

        if (!fileStorage.objectExists(fileKey)) {
            throw ResourceNotFoundException(
                resource = "File",
                identifier = fileKey,
                errorCode = ErrorCodes.RESOURCE_NOT_FOUND,
            )
        }

        val downloadUrl = fileStorage.generateDownloadUrl(fileKey)
        val fileName = fileKey.substringAfterLast('/')

        return FileDownloadInfo(
            downloadUrl = downloadUrl,
            fileName = fileName,
            expiresInSeconds = urlExpirationSeconds,
        )
    }

    override suspend fun checkFileExists(fileKey: String): Boolean = fileStorage.objectExists(fileKey)

    override suspend fun deleteFile(fileKey: String): Boolean {
        logger.debug { "Deleting file: $fileKey" }

        if (!fileStorage.objectExists(fileKey)) {
            logger.warn { "Attempted to delete non-existent file: $fileKey" }
            return false
        }

        return try {
            val result = fileStorage.deleteObject(fileKey)
            if (result) {
                logger.info { "Successfully deleted file: $fileKey" }
            } else {
                logger.warn { "Failed to delete file: $fileKey" }
            }
            result
        } catch (e: Exception) {
            logger.error(e) { "Error deleting file: $fileKey" }
            false
        }
    }

    private fun validateFileSize(fileSize: Int) {
        val maxSizeBytes = 10_000_000L // 10 MB
        if (fileSize <= 0 || fileSize > maxSizeBytes) {
            throw FileValidationException(
                message = "Invalid file size. The maximum size is ${maxSizeBytes / 1_000_000} MB.",
                errorCode = ErrorCodes.FILE_VALIDATION_ERROR,
            )
        }
    }

    private fun validateContentType(contentType: String) {
        val allowedTypes = listOf("image/jpeg", "image/png", "application/pdf", "text/plain")
        if (contentType !in allowedTypes) {
            throw FileValidationException(
                message = "Unsupported file type. Allowed types are: ${allowedTypes.joinToString()}",
                errorCode = ErrorCodes.FILE_VALIDATION_ERROR,
            )
        }
    }
}
