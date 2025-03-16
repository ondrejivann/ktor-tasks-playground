package infrastructure.persistence.table

import domain.model.Priority
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.postgresql.util.PGobject

object TaskTable : IntIdTable("tasks") {
    val name = varchar("name", 128).uniqueIndex()
    val description = varchar("description", 1024)
    val statusId = reference("status_id", TaskStatusTable)
    val priority = customEnumeration(
        name = "priority",
        sql ="task_priority",
        fromDb = { value ->
            Priority.valueOf(value.toString().uppercase())
        },
        toDb = { PGobject().apply { type = "task_priority"; value = it.name.lowercase() } }
    )
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}