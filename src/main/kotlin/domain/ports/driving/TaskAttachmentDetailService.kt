package domain.ports.driving

import domain.model.TaskAttachmentDetail
import domain.model.TaskAttachmentUploadInfo

interface TaskAttachmentDetailService {
    suspend fun getAttachmentsDetailsForTask(taskId: Int): List<TaskAttachmentDetail>

    suspend fun prepareUploadForTask(taskId: Int, fileName: String, contentType: String, fileSize: Int): TaskAttachmentUploadInfo

    suspend fun confirmAttachmentUpload(taskId: Int, fileKey: String): TaskAttachmentDetail

    suspend fun removeAttachment(id: Int): Boolean

    suspend fun generateDownloadUrlForAttachment(id: Int): String
}
