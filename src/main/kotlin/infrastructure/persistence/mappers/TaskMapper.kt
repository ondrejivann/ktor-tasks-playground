package infrastructure.persistence.mappers

import domain.model.Task
import domain.model.TaskAttachment
import infrastructure.persistence.dao.TaskAttachmentDAO
import infrastructure.persistence.dao.TaskDAO
import infrastructure.persistence.dao.taskStatusDaoToModel
import infrastructure.persistence.table.TaskAttachmentTable

fun taskDaoToModelWithStatus(dao: TaskDAO): Task {
    val attachments = TaskAttachmentDAO
        .find { TaskAttachmentTable.taskId eq dao.id }
        .map { attachmentDao ->
            TaskAttachment(
                id = attachmentDao.id.value,
                taskId = dao.id.value,
                fileKey = attachmentDao.fileKey,
                fileName = attachmentDao.fileName,
                contentType = attachmentDao.contentType,
                uploadStatus = attachmentDao.uploadStatus,
            )
        }

    return Task(
        id = dao.id.value,
        name = dao.name,
        description = dao.description,
        priority = dao.priority,
        status = dao.status.taskStatusDaoToModel(),
        attachments = attachments,
    )
}