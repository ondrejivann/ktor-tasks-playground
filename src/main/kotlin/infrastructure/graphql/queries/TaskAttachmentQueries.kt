package infrastructure.graphql.queries

import domain.exceptions.EntityNotFoundException
import domain.exceptions.ValidationException
import domain.ports.driving.TaskAttachmentDetailService
import infrastructure.graphql.model.TaskAttachmentDetailGQL
import infrastructure.graphql.model.TaskAttachmentDownloadResponseGQL
import infrastructure.graphql.model.createDownloadUrlErrorResponse
import infrastructure.graphql.model.toGQL
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskAttachmentQueries (
    private val taskAttachmentDetailService: TaskAttachmentDetailService,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachmentDetailGQL> {
        return try {
            taskAttachmentDetailService.getAttachmentsDetailsForTask(taskId).map { it.toGQL() }
        } catch (e: ValidationException) {
            logger.warn { "Invalid task ID: ${e.message}" }
            emptyList()
        } catch (e: EntityNotFoundException) {
            logger.warn { "Task not found: ${e.message}" }
            emptyList()
        } catch (e: Exception) {
            logger.error(e) { "Error fetching attachments: ${e.message}" }
            emptyList()
        }
    }

    suspend fun getAttachmentDownloadUrl(attachmentId: Int): TaskAttachmentDownloadResponseGQL {
        return try {
            val downloadUrl = taskAttachmentDetailService.generateDownloadUrlForAttachment(attachmentId)
            TaskAttachmentDownloadResponseGQL(
                downloadUrl = downloadUrl
            )
        } catch (e: ValidationException) {
            logger.warn { "Validation error generating download URL: ${e.message}" }
            createDownloadUrlErrorResponse("Validation error: ${e.message}")
        } catch (e: EntityNotFoundException) {
            logger.warn { "Entity not found generating download URL: ${e.message}" }
            createDownloadUrlErrorResponse("Attachment not found: ${e.message}")
        } catch (e: Exception) {
            logger.error(e) { "Error generating download URL: ${e.message}" }
            createDownloadUrlErrorResponse("Unexpected error: ${e.message}")
        }
    }
}