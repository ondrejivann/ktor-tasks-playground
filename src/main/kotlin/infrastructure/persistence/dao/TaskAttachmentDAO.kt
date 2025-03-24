package infrastructure.persistence.dao

import infrastructure.persistence.table.TaskAttachmentTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TaskAttachmentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskAttachmentDAO>(TaskAttachmentTable)

    var taskId by TaskAttachmentTable.taskId
    var fileKey by TaskAttachmentTable.fileKey
    var fileName by TaskAttachmentTable.fileName
    var contentType by TaskAttachmentTable.contentType
}