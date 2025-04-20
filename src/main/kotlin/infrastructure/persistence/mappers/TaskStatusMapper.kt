package infrastructure.persistence.mappers

import domain.model.TaskStatus
import infrastructure.persistence.dao.TaskStatusDAO

fun statusDaoToModel(dao: TaskStatusDAO) = TaskStatus(
    id = dao.id.value,
    code = dao.code,
    name = dao.name,
    description = dao.description,
    color = dao.color,
    icon = dao.icon,
    displayOrder = dao.displayOrder,
)
