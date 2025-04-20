package application.services

import application.exceptions.BusinessRuleViolationException
import common.exceptions.ErrorCodes
import domain.exceptions.EntityNotFoundException
import domain.model.TaskAttachment
import domain.model.TaskAttachmentDetail
import domain.model.TaskAttachmentUploadInfo
import domain.model.UploadStatus
import domain.ports.driven.TaskRepository
import domain.ports.driving.FileService
import domain.ports.driving.TaskAttachmentDetailService
import domain.ports.driving.TaskAttachmentService
import org.koin.core.annotation.Single

@Single(binds = [TaskAttachmentDetailService::class])
class TaskAttachmentDetailServiceImpl(
    private val taskAttachmentService: TaskAttachmentService,
    private val taskRepository: TaskRepository,
    private val fileService: FileService,
) : TaskAttachmentDetailService {
    override suspend fun getAttachmentsDetailsForTask(taskId: Int): List<TaskAttachmentDetail> {
        val attachments = taskAttachmentService.getAttachmentsForTask(taskId)

        return attachments.map { attachment ->
            val fileDownloadInfo = fileService.generateDownloadLink(attachment.fileKey)

            TaskAttachmentDetail(
                id = attachment.id,
                fileKey = attachment.fileKey,
                fileName = attachment.fileName,
                contentType = attachment.contentType,
                downloadUrl = fileDownloadInfo.downloadUrl,
            )
        }
    }

    override suspend fun prepareUploadForTask(
        taskId: Int,
        fileName: String,
        contentType: String,
        fileSize: Int,
    ): TaskAttachmentUploadInfo {
        taskRepository.taskById(taskId)
            ?: throw EntityNotFoundException("Task", taskId, errorCode = ErrorCodes.ENTITY_NOT_FOUND)

        val uploadInfo = fileService.prepareFileUpload(fileName, contentType, fileSize)

        val attachment =
            TaskAttachment(
                taskId = taskId,
                fileKey = uploadInfo.fileKey,
                fileName = fileName,
                contentType = contentType,
                uploadStatus = UploadStatus.PENDING,
            )

        val taskAttachment = taskAttachmentService.addTaskAttachment(attachment)

        return TaskAttachmentUploadInfo(
            id = taskAttachment.id,
            fileKey = uploadInfo.fileKey,
            uploadUrl = uploadInfo.uploadUrl,
            expiresInSeconds = uploadInfo.expiresInSeconds,
        )
    }

    override suspend fun confirmAttachmentUpload(taskId: Int, fileKey: String): TaskAttachmentDetail {
        taskRepository.taskById(taskId)
            ?: throw EntityNotFoundException("Task", taskId, errorCode = ErrorCodes.ENTITY_NOT_FOUND)

        val attachment =
            taskAttachmentService.getAttachmentByFileKey(fileKey)
                ?: throw EntityNotFoundException("Attachment", fileKey, errorCode = ErrorCodes.ENTITY_NOT_FOUND)

        if (!fileService.checkFileExists(fileKey)) {
            taskAttachmentService.updateAttachmentUploadStatus(attachment.id, UploadStatus.FAILED)
            throw BusinessRuleViolationException(
                message = "File not found or upload failed",
                errorCode = ErrorCodes.BUSINESS_RULE_VIOLATION,
            )
        }

        val fileDownloadInfo = fileService.generateDownloadLink(attachment.fileKey)

        if (taskAttachmentService.updateAttachmentUploadStatus(attachment.id, UploadStatus.UPLOADED)) {
            return TaskAttachmentDetail(
                id = attachment.id,
                fileKey = attachment.fileKey,
                fileName = attachment.fileName,
                contentType = attachment.contentType,
                downloadUrl = fileDownloadInfo.downloadUrl,
            )
        } else {
            throw EntityNotFoundException("Attachment", attachment.id, errorCode = ErrorCodes.ENTITY_NOT_FOUND)
        }
    }

    override suspend fun removeAttachment(id: Int): Boolean {
        val attachment =
            taskAttachmentService.getTaskAttachmentById(id)
                ?: throw EntityNotFoundException("Attachment", id, errorCode = ErrorCodes.ENTITY_NOT_FOUND)

        fileService.deleteFile(attachment.fileKey)

        return taskAttachmentService.removeAttachment(id)
    }

    override suspend fun generateDownloadUrlForAttachment(id: Int): String {
        val attachment =
            taskAttachmentService.getTaskAttachmentById(id)
                ?: throw EntityNotFoundException("Attachment", id, errorCode = ErrorCodes.ENTITY_NOT_FOUND)

        return fileService.generateDownloadLink(attachment.fileKey).downloadUrl
    }
}
