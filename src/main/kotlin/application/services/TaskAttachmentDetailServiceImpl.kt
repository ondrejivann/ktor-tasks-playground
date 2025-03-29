package application.services

import application.services.TaskAttachmentServiceImpl.*
import domain.model.TaskAttachment
import domain.model.TaskAttachmentDetail
import domain.model.UploadStatus
import domain.model.file.FileUploadInfo
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
                downloadUrl = fileDownloadInfo.downloadUrl
            )
        }
    }

    override suspend fun prepareUploadForTask(
        taskId: Int,
        fileName: String,
        contentType: String,
        fileSize: Int
    ): FileUploadInfo {
        taskRepository.taskById(taskId) ?: throw TaskNotFoundException("Task with ID $taskId not found")

        val uploadInfo = fileService.prepareFileUpload(fileName, contentType, fileSize)

        val attachment = TaskAttachment(
            taskId = taskId,
            fileKey = uploadInfo.fileKey,
            fileName = fileName,
            contentType = contentType,
            uploadStatus = UploadStatus.PENDING,
        )

        taskAttachmentService.addTaskAttachment(attachment)

        return uploadInfo
    }

    override suspend fun confirmAttachmentUpload(taskId: Int, fileKey: String): TaskAttachmentDetail {
        taskRepository.taskById(taskId) ?: throw TaskNotFoundException("Task with ID $taskId not found")

        val attachment = taskAttachmentService.getAttachmentByFileKey(fileKey)
            ?: throw AttachmentNotFoundException("Attachment with fileKey $fileKey not found")

        if (!fileService.checkFileExists(fileKey)) {
            taskAttachmentService.updateAttachmentUploadStatus(attachment.id, UploadStatus.FAILED)
            throw AttachmentException("File not found or upload failed")
        }

        val fileDownloadInfo = fileService.generateDownloadLink(attachment.fileKey)

        if (taskAttachmentService.updateAttachmentUploadStatus(attachment.id, UploadStatus.UPLOADED)) {
            return TaskAttachmentDetail(
                id = attachment.id,
                fileKey = attachment.fileKey,
                fileName = attachment.fileName,
                contentType = attachment.contentType,
                downloadUrl = fileDownloadInfo.downloadUrl
            )
        } else {
            throw AttachmentException("Attachment not found")
        }
    }

    override suspend fun removeAttachment(id: Int): Boolean {
        val attachment = taskAttachmentService.getTaskAttachmentById(id)
            ?: throw AttachmentNotFoundException("Attachment with ID $id not found")

        fileService.deleteFile(attachment.fileKey)

        return taskAttachmentService.removeAttachment(id)
    }

    override suspend fun generateDownloadUrlForAttachment(id: Int): String {
        val attachment = taskAttachmentService.getTaskAttachmentById(id)
            ?: throw AttachmentNotFoundException("Attachment with ID $id not found")

        return fileService.generateDownloadLink(attachment.fileKey).downloadUrl
    }
}