package domain.ports.driven

import domain.model.TaskAttachment
import domain.model.UploadStatus

interface TaskAttachmentRepository {
    suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachment>

    suspend fun addAttachment(attachment: TaskAttachment): TaskAttachment

    suspend fun removeAttachment(id: Int): Boolean

    suspend fun updateAttachmentUploadStatus(id: Int, uploadStatus: UploadStatus): Boolean

    suspend fun getAttachmentById(id: Int): TaskAttachment?

    suspend fun getAttachmentByFileKey(fileKey: String): TaskAttachment?
}
