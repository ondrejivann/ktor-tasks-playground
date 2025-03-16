package infrastructure.persistence.dao

import domain.model.TaskStatus
import infrastructure.persistence.table.TaskStatusTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TaskStatusDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskStatusDAO>(TaskStatusTable)

    var code by TaskStatusTable.code
    var name by TaskStatusTable.name
    var description by TaskStatusTable.description
    var color by TaskStatusTable.color
    var icon by TaskStatusTable.icon
    var displayOrder by TaskStatusTable.displayOrder
}

fun TaskStatusDAO.taskStatusDaoToModel() = TaskStatus(
    id = id.value,
    code = code,
    name = name,
    description = description,
    color = color,
    icon = icon,
    displayOrder = displayOrder,
)