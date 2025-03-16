package infrastructure.persistence.mappers

import domain.model.Task
import infrastructure.persistence.dao.TaskDAO
import infrastructure.persistence.dao.taskStatusDaoToModel

fun taskDaoToModelWithStatus(dao: TaskDAO) = Task(
    id = dao.id.value,
    name = dao.name,
    description = dao.description,
    priority = dao.priority,
    status = dao.status.taskStatusDaoToModel()
)