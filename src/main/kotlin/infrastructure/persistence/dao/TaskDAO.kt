package infrastructure.persistence.dao

import infrastructure.persistence.table.TaskTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TaskTable)

    var name by TaskTable.name
    var description by TaskTable.description
    var statusId by TaskTable.statusId
    var status by TaskStatusDAO referencedOn TaskTable.statusId
    var priority by TaskTable.priority
    var createdAt by TaskTable.createdAt
    var updatedAt by TaskTable.updatedAt
}