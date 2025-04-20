package infrastructure.persistence.table

import org.jetbrains.exposed.dao.id.IntIdTable

object TaskStatusTable : IntIdTable("task_statuses") {
    val code = varchar("code", 20).uniqueIndex()
    val name = varchar("name", 50)
    val description = text("description").nullable()
    val color = varchar("color", 20).nullable()
    val icon = varchar("icon", 50).nullable()
    val displayOrder = integer("display_order")
}
