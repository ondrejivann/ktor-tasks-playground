package application.services

import application.exceptions.BusinessRuleViolationException
import common.exceptions.ErrorCodes
import domain.model.TaskAttachment
import domain.model.UploadStatus
import domain.ports.driven.TaskAttachmentRepository
import domain.ports.driving.TaskAttachmentService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskAttachmentServiceImpl(private val taskAttachmentRepository: TaskAttachmentRepository) : TaskAttachmentService {
    private val logger = KotlinLogging.logger {}

    override suspend fun getTaskAttachmentById(id: Int): TaskAttachment? = taskAttachmentRepository.getAttachmentById(id)

    override suspend fun getAttachmentByFileKey(fileKey: String): TaskAttachment? =
        taskAttachmentRepository.getAttachmentByFileKey(fileKey)

    override suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachment> =
        taskAttachmentRepository.getAttachmentsForTask(taskId)

    override suspend fun addTaskAttachment(taskAttachment: TaskAttachment): TaskAttachment {
        logger.debug { "Creating new task attachment: ${taskAttachment.fileKey}" }

        try {
            taskAttachmentRepository.addAttachment(taskAttachment).also {
                logger.debug { "Task attachment '${it.fileKey}' created successfully" }
            }
        } catch (e: IllegalStateException) {
            logger.error { "Failed to create task attachment: ${e.message}" }
            throw BusinessRuleViolationException(
                message = "Failed to create task attachment: ${e.message}",
                cause = e,
                errorCode = ErrorCodes.BUSINESS_RULE_VIOLATION,
            )
        }

        return taskAttachmentRepository.addAttachment(taskAttachment)
    }

    override suspend fun updateAttachmentUploadStatus(id: Int, uploadStatus: UploadStatus): Boolean =
        taskAttachmentRepository.updateAttachmentUploadStatus(id, uploadStatus)

    override suspend fun removeAttachment(id: Int): Boolean {
        taskAttachmentRepository.getAttachmentById(id) ?: return false

        return taskAttachmentRepository.removeAttachment(id)
    }
}
