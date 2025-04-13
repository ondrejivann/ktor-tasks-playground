package domain.ports.driving

import domain.model.TaskAttachment
import domain.model.UploadStatus

interface TaskAttachmentService {
    suspend fun getTaskAttachmentById(id: Int): TaskAttachment?
    suspend fun getAttachmentByFileKey(fileKey: String): TaskAttachment?
    suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachment>
    suspend fun addTaskAttachment(taskAttachment: TaskAttachment): TaskAttachment
    suspend fun updateAttachmentUploadStatus(id: Int, uploadStatus: UploadStatus): Boolean
    suspend fun removeAttachment(id: Int): Boolean
}