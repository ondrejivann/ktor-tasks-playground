package infrastructure.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import domain.ports.driving.TaskAttachmentDetailService
import infrastructure.graphql.model.TaskAttachmentDetailGQL
import infrastructure.graphql.model.TaskAttachmentDownloadResponseGQL
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskAttachmentQueries(private val taskAttachmentDetailService: TaskAttachmentDetailService) {
    private val logger = KotlinLogging.logger {}

    suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachmentDetailGQL> =
        taskAttachmentDetailService.getAttachmentsDetailsForTask(taskId).map {
            TaskAttachmentDetailGQL(
                id = ID(it.id.toString()),
                fileName = it.fileName,
                contentType = it.contentType,
                downloadUrl = it.downloadUrl,
            )
        }

    suspend fun getAttachmentDownloadUrl(attachmentId: Int): TaskAttachmentDownloadResponseGQL {
        val downloadUrl = taskAttachmentDetailService.generateDownloadUrlForAttachment(attachmentId)
        return TaskAttachmentDownloadResponseGQL(
            downloadUrl = downloadUrl,
        )
    }
}
