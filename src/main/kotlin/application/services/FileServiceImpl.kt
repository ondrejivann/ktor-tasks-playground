package application.services

import domain.model.file.FileDownloadInfo
import domain.model.file.FileUploadInfo
import domain.ports.FileService
import domain.ports.FileStoragePort
import infrastructure.aws.s3.S3Config
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.config.*
import org.koin.core.annotation.Single
import java.util.*

@Single
class FileServiceImpl(
    private val fileStorage: FileStoragePort,
    private val config: ApplicationConfig,
) : FileService {
    private val logger = KotlinLogging.logger {}

    private val urlExpirationSeconds = config.property(S3Config.AWS_URL_EXPIRATION_SECONDS_PATH)
        .getString().toLong()

    override suspend fun prepareFileUpload(fileName: String, contentType: String, fileSize: Long): FileUploadInfo {
        logger.debug { "Preparing upload for file: $fileName, type: $contentType, size: $fileSize" }

        // Validation
        validateFileSize(fileSize)
        validateContentType(contentType)

        // Generating a key for a file
        val fileExtension = fileName.substringAfterLast('.', "")
        val fileKey = "${S3Config.UPLOADS_PREFIX}${UUID.randomUUID()}${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"

        // Metadata for the file
        val metadata = mapOf(
            S3Config.METADATA_ORIGINAL_FILENAME to fileName,
        )

        // Generate URL for upload
        val uploadUrl = fileStorage.generateUploadUrl(fileKey, contentType, metadata)

        return FileUploadInfo(
            uploadUrl = uploadUrl,
            fileKey = fileKey,
            expiresInSeconds = urlExpirationSeconds
        )
    }

    override suspend fun generateDownloadLink(fileKey: String): FileDownloadInfo {
        logger.debug { "Generating download link for file: $fileKey" }

        if (!fileStorage.objectExists(fileKey)) {
            throw FileNotFoundException("The requested file was not found")
        }

        val downloadUrl = fileStorage.generateDownloadUrl(fileKey)
        val fileName = fileKey.substringAfterLast('/')

        return FileDownloadInfo(
            downloadUrl = downloadUrl,
            fileName = fileName,
            expiresInSeconds = urlExpirationSeconds
        )
    }

    override suspend fun checkFileExists(fileKey: String): Boolean {
        return fileStorage.objectExists(fileKey)
    }

    private fun validateFileSize(fileSize: Long) {
        val maxSizeBytes = 10_000_000L // 10 MB
        if (fileSize <= 0 || fileSize > maxSizeBytes) {
            throw InvalidFileException("Invalid file size. The maximum size is ${maxSizeBytes/1_000_000} MB.")
        }
    }

    private fun validateContentType(contentType: String) {
        val allowedTypes = listOf("image/jpeg", "image/png", "application/pdf", "text/plain")
        if (contentType !in allowedTypes) {
            throw InvalidFileException("Unsupported file type. Allowed types are: ${allowedTypes.joinToString()}")
        }
    }

    class InvalidFileException(message: String) : Exception(message)
    class FileNotFoundException(message: String) : Exception(message)
}