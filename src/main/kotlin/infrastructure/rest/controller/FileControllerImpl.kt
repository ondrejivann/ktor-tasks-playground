package infrastructure.rest.controller

import common.exceptions.ErrorCodes
import domain.exceptions.ValidationException
import domain.ports.driving.FileService
import infrastructure.exceptions.FileStorageException
import infrastructure.rest.dto.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.annotation.Single

@Single(binds = [FileController::class])
class FileControllerImpl(
    private val fileService: FileService,
) : FileController {
    override suspend fun prepareFileUpload(call: ApplicationCall) {
        val request = call.receive<FileUploadRequest>()

        val fileUploadInfo = fileService.prepareFileUpload(
            fileName = request.fileName,
            contentType = request.contentType,
            fileSize = request.fileSize
        )

        call.respond(
            HttpStatusCode.OK,
            FileUploadResponse(
                uploadUrl = fileUploadInfo.uploadUrl,
                fileKey = fileUploadInfo.fileKey,
                expiresInSeconds = fileUploadInfo.expiresInSeconds
            )
        )
    }

    override suspend fun getDownloadLink(call: ApplicationCall) {
        val request = call.receive<FileDownloadRequest>()

        val downloadInfo = fileService.generateDownloadLink(request.fileKey)

        call.respond(
            HttpStatusCode.OK,
            FileDownloadResponse(
                downloadUrl = downloadInfo.downloadUrl,
                fileName = downloadInfo.fileName,
                expiresInSeconds = downloadInfo.expiresInSeconds
            )
        )
    }

    override suspend fun checkFileExists(call: ApplicationCall) {
        val fileKey = call.parameters["fileKey"]
            ?: throw ValidationException("Missing fileKey parameter", errorCode = ErrorCodes.VALIDATION_ERROR)

        val exists = fileService.checkFileExists(fileKey)

        call.respond(
            HttpStatusCode.OK,
            mapOf("exists" to exists)
        )
    }

    override suspend fun removeFile(call: ApplicationCall) {
        val request = call.receive<FileDeleteRequest>()

        val result = fileService.deleteFile(request.fileKey)

        if (result) {
            call.respond(HttpStatusCode.OK)
        } else {
            throw FileStorageException(
                message = "Failed to remove the file",
                errorCode = ErrorCodes.FILE_STORAGE_ERROR
            )
        }
    }
}