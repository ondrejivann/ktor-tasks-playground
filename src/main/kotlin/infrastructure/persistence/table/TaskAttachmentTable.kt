package infrastructure.persistence.table

import org.jetbrains.exposed.dao.id.IntIdTable

object TaskAttachmentTable : IntIdTable("task_attachments") {
    val taskId = reference("task_id", TaskTable)
    val fileKey = varchar("file_key", 255)
    val fileName = varchar("file_name", 255)
    val contentType = varchar("content_type", 100)
}