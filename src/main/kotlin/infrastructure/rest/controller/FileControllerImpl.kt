package infrastructure.rest.controller

import application.services.FileServiceImpl
import application.services.FileServiceImpl.FileNotFoundException
import application.services.FileServiceImpl.InvalidFileException
import domain.ports.FileService
import infrastructure.rest.dto.ErrorResponse
import infrastructure.rest.dto.FileDownloadRequest
import infrastructure.rest.dto.FileDownloadResponse
import infrastructure.rest.dto.FileUploadRequest
import infrastructure.rest.dto.FileUploadResponse
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
        try {
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
        } catch (e: InvalidFileException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(message = e.message ?: "File validation error", code = "INVALID_FILE")
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(message = "Failed to prepare file upload")
            )
        }
    }

    override suspend fun getDownloadLink(call: ApplicationCall) {
        try {
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
        } catch (e: FileNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(message = e.message ?: "File not found", code = "FILE_NOT_FOUND")
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(message = "Unable to get download link")
            )
        }
    }

    override suspend fun checkFileExists(call: ApplicationCall) {
        try {
            val fileKey = call.parameters["fileKey"]
                ?: return call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "Missing fileKey parameter", code = "MISSING_PARAMETER")
                )

            val exists = fileService.checkFileExists(fileKey)

            call.respond(
                HttpStatusCode.OK,
                mapOf("exists" to exists)
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(message = "Failed to verify the existence of the file")
            )
        }
    }
}