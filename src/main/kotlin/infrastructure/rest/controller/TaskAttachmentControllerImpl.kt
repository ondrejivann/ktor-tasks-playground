package infrastructure.rest.controller

import application.services.TaskAttachmentServiceImpl.AttachmentNotFoundException
import application.services.TaskAttachmentServiceImpl.TaskNotFoundException
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
        try {
            val taskId = call.parameters["taskId"]?.toIntOrNull()
                ?: return call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid task ID", "INVALID_TASK_ID")
                )

            val attachmentsDetails = taskAttachmentDetailService.getAttachmentsDetailsForTask(taskId)

            call.respond(
                HttpStatusCode.OK,
                attachmentsDetails,
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error when retrieving attachments")
            )
        }
    }

    override suspend fun prepareAttachmentUpload(call: ApplicationCall) {
        try {
            val taskId = call.parameters["taskId"]?.toIntOrNull()
                ?: return call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid task ID", "INVALID_TASK_ID")
                )

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
        } catch (e: TaskNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(e.message ?: "Task not found", "TASK_NOT_FOUND")
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error when preparing to upload an attachment")
            )
        }
    }

    override suspend fun confirmAttachmentUpload(call: ApplicationCall) {
        try {
            val taskId = call.parameters["taskId"]?.toIntOrNull()
                ?: return call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid task ID", "INVALID_TASK_ID")
                )

            val request = call.receive<ConfirmTaskAttachmentUploadRequest>()

            val attachmentDetail = taskAttachmentDetailService.confirmAttachmentUpload(
                taskId = taskId,
                fileKey = request.fileKey
            )

            call.respond(
                HttpStatusCode.OK,
                attachmentDetail,
            )
        } catch (e: TaskNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(e.message ?: "Task not found", "TASK_NOT_FOUND")
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error when confirming attachment upload")
            )
        }
    }

    override suspend fun removeAttachment(call: ApplicationCall) {
        try {
            val attachmentId = call.parameters["attachmentId"]?.toIntOrNull()
                ?: return call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid attachment ID", "INVALID_ATTACHMENT_ID")
                )

            val success = taskAttachmentDetailService.removeAttachment(attachmentId)

            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Attachment not found", "ATTACHMENT_NOT_FOUND")
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error deleting attachment")
            )
        }
    }

    override suspend fun getAttachmentDownloadUrl(call: ApplicationCall) {
        try {
            val attachmentId = call.parameters["attachmentId"]?.toIntOrNull()
                ?: return call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid Attachment ID", "INVALID_ATTACHMENT_ID")
                )

            val downloadUrl = taskAttachmentDetailService.generateDownloadUrlForAttachment(attachmentId)

            call.respond(
                HttpStatusCode.OK,
                TaskAttachmentDownloadResponse(downloadUrl = downloadUrl)
            )
        } catch (e: AttachmentNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(e.message ?: "Attachment not found", "ATTACHMENT_NOT_FOUND")
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error getting download URL")
            )
        }
    }
}