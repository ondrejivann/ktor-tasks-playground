package infrastructure.graphql.mutations

import domain.exceptions.EntityNotFoundException
import domain.exceptions.ValidationException
import domain.ports.driving.TaskAttachmentDetailService
import infrastructure.graphql.model.*
import infrastructure.graphql.model.toGQL
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskAttachmentMutations(
    private val taskAttachmentDetailService: TaskAttachmentDetailService,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun prepareAttachmentUpload(
        taskId: Int,
        input: PrepareTaskAttachmentUploadRequestGQL
    ): PrepareTaskAttachmentUploadResponseGQL {
        return try {
            val uploadInfo = taskAttachmentDetailService.prepareUploadForTask(
                taskId = taskId,
                fileName = input.fileName,
                contentType = input.contentType,
                fileSize = input.fileSize
            )
            uploadInfo.toGQL()
        } catch (e: ValidationException) {
            logger.warn { "Validation error: ${e.message}" }
            createPrepareUploadErrorResponse("Validation error: ${e.message}")
        } catch (e: EntityNotFoundException) {
            logger.warn { "Entity not found: ${e.message}" }
            createPrepareUploadErrorResponse("Entity not found: ${e.message}")
        } catch (e: Exception) {
            logger.error(e) { "Error preparing upload: ${e.message}" }
            createPrepareUploadErrorResponse("Unexpected error: ${e.message}")
        }
    }

    suspend fun confirmAttachmentUpload(
        taskId: Int,
        fileKey: String
    ): TaskAttachmentDetailGQL? {
        return try {
            val attachmentDetail = taskAttachmentDetailService.confirmAttachmentUpload(
                taskId = taskId,
                fileKey = fileKey
            )
            attachmentDetail.toGQL()
        } catch (e: ValidationException) {
            logger.warn { "Validation error confirming upload: ${e.message}" }
            null
        } catch (e: EntityNotFoundException) {
            logger.warn { "Entity not found confirming upload: ${e.message}" }
            null
        } catch (e: Exception) {
            logger.error(e) { "Error confirming upload: ${e.message}" }
            null
        }
    }

    suspend fun removeAttachment(attachmentId: Int): TaskAttachmentOperationResultGQL {
        return try {
            val success = taskAttachmentDetailService.removeAttachment(attachmentId)
            if (success) {
                TaskAttachmentOperationResultGQL(
                    success = true,
                    message = "Attachment successfully removed",
                    attachmentId = attachmentId
                )
            } else {
                TaskAttachmentOperationResultGQL(
                    success = false,
                    message = "Attachment not found or could not be removed"
                )
            }
        } catch (e: ValidationException) {
            logger.warn { "Validation error removing attachment: ${e.message}" }
            createAttachmentErrorResponse("Validation error: ${e.message}")
        } catch (e: EntityNotFoundException) {
            logger.warn { "Entity not found removing attachment: ${e.message}" }
            createAttachmentErrorResponse("Attachment not found: ${e.message}")
        } catch (e: Exception) {
            logger.error(e) { "Error removing attachment: ${e.message}" }
            createAttachmentErrorResponse("Unexpected error: ${e.message}")
        }
    }
}