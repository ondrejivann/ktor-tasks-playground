package domain.ports

import domain.model.TaskAttachment

interface TaskAttachmentRepository {
    suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachment>
    suspend fun addAttachment(attachment: TaskAttachment): TaskAttachment
    suspend fun removeAttachment(id: Int): Boolean
    suspend fun getAttachmentById(id: Int): TaskAttachment?
} 