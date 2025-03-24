package infrastructure.persistence.repository

import domain.model.TaskAttachment
import domain.ports.TaskAttachmentRepository
import infrastructure.persistence.common.suspendTransaction
import infrastructure.persistence.dao.TaskAttachmentDAO
import infrastructure.persistence.table.TaskAttachmentTable
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.core.annotation.Single

@Single
class TaskAttachmentRepositoryImpl : TaskAttachmentRepository {
    override suspend fun getAttachmentsForTask(taskId: Int): List<TaskAttachment> = suspendTransaction {
        TaskAttachmentDAO.find { TaskAttachmentTable.taskId eq taskId }
            .map { dao ->
                TaskAttachment(
                    id = dao.id.value,
                    taskId = dao.taskId.value,
                    fileKey = dao.fileKey,
                    fileName = dao.fileName,
                    contentType = dao.contentType,
                )
            }
    }

    override suspend fun addAttachment(attachment: TaskAttachment): TaskAttachment = suspendTransaction {
        val dao = TaskAttachmentDAO.new {
            taskId = EntityID(attachment.taskId, TaskAttachmentTable)
            fileKey = attachment.fileKey
            fileName = attachment.fileName
            contentType = attachment.contentType
        }

        TaskAttachment(
            id = dao.id.value,
            taskId = dao.taskId.value,
            fileKey = dao.fileKey,
            fileName = dao.fileName,
            contentType = dao.contentType,
        )
    }

    override suspend fun removeAttachment(id: Int): Boolean = suspendTransaction {
        val count = TaskAttachmentDAO.findById(id)?.let {
            it.delete()
            1
        } ?: 0

        count > 0
    }

    override suspend fun getAttachmentById(id: Int): TaskAttachment? = suspendTransaction {
        TaskAttachmentDAO.findById(id)?.let { dao ->
            TaskAttachment(
                id = dao.id.value,
                taskId = dao.taskId.value,
                fileKey = dao.fileKey,
                fileName = dao.fileName,
                contentType = dao.contentType,
            )
        }
    }
}