package infrastructure.rest.controller

import common.exceptions.ErrorCodes
import domain.exceptions.ValidationException
import domain.ports.driving.TaskAttachmentDetailService
import infrastructure.rest.dto.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.annotation.Single

@Single(binds = [TaskAttachmentController::class])
class TaskAttachmentControllerImpl(
    private val taskAttachmentDetailService: TaskAttachmentDetailService,
) : TaskAttachmentController {

    override suspend fun getAttachmentsForTask(call: ApplicationCall) {
        val taskId = call.parameters["taskId"]?.toIntOrNull()
            ?: throw ValidationException("Invalid task ID", errorCode = ErrorCodes.VALIDATION_ERROR)

        val attachmentsDetails = taskAttachmentDetailService.getAttachmentsDetailsForTask(taskId)

        call.respond(
            HttpStatusCode.OK,
            attachmentsDetails,
        )
    }

    override suspend fun prepareAttachmentUpload(call: ApplicationCall) {
        val taskId = call.parameters["taskId"]?.toIntOrNull()
            ?: throw ValidationException("Invalid task ID", errorCode = ErrorCodes.VALIDATION_ERROR)

        val request = call.receive<PrepareTaskAttachmentUploadRequest>()

        val fileUploadInfo = taskAttachmentDetailService.prepareUploadForTask(
            taskId = taskId,
            fileName = request.fileName,
            contentType = request.contentType,
            fileSize = request.fileSize
        )

        call.respond(
            HttpStatusCode.OK,
            PrepareTaskAttachmentUploadResponse(
                uploadUrl = fileUploadInfo.uploadUrl,
                fileKey = fileUploadInfo.fileKey,
                expiresInSeconds = fileUploadInfo.expiresInSeconds
            )
        )
    }

    override suspend fun confirmAttachmentUpload(call: ApplicationCall) {
        val taskId = call.parameters["taskId"]?.toIntOrNull()
            ?: throw ValidationException("Invalid task ID", errorCode = ErrorCodes.VALIDATION_ERROR)

        val request = call.receive<ConfirmTaskAttachmentUploadRequest>()

        val attachmentDetail = taskAttachmentDetailService.confirmAttachmentUpload(
            taskId = taskId,
            fileKey = request.fileKey
        )

        call.respond(
            HttpStatusCode.OK,
            attachmentDetail,
        )
    }

    override suspend fun removeAttachment(call: ApplicationCall) {
        val attachmentId = call.parameters["attachmentId"]?.toIntOrNull()
            ?: throw ValidationException("Invalid attachment ID", errorCode = ErrorCodes.VALIDATION_ERROR)

        val success = taskAttachmentDetailService.removeAttachment(attachmentId)

        if (success) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            // Tady by měla entita existovat, protože jinak by metoda vyhodila výjimku
            // Takže toto by nemělo nastat, ale pro jistotu necháváme
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("Attachment not found", ErrorCodes.ENTITY_NOT_FOUND)
            )
        }
    }

    override suspend fun getAttachmentDownloadUrl(call: ApplicationCall) {
        val attachmentId = call.parameters["attachmentId"]?.toIntOrNull()
            ?: throw ValidationException("Invalid Attachment ID", errorCode = ErrorCodes.VALIDATION_ERROR)

        val downloadUrl = taskAttachmentDetailService.generateDownloadUrlForAttachment(attachmentId)

        call.respond(
            HttpStatusCode.OK,
            TaskAttachmentDownloadResponse(downloadUrl = downloadUrl)
        )
    }
}