package infrastructure.persistence.repository

import domain.model.TaskStatus
import domain.ports.TaskStatusRepository
import infrastructure.persistence.dao.TaskStatusDAO
import infrastructure.persistence.mappers.statusDaoToModel
import infrastructure.persistence.common.suspendTransaction
import infrastructure.persistence.table.TaskStatusTable
import org.koin.core.annotation.Single

@Single
class TaskStatusRepositoryImpl : TaskStatusRepository {
    override suspend fun getById(id: Int): TaskStatus? = suspendTransaction {
        TaskStatusDAO.find { TaskStatusTable.id eq id }
            .firstOrNull()
            ?.let(::statusDaoToModel)
    }

    override suspend fun getByCode(code: String): TaskStatus? = suspendTransaction {
        TaskStatusDAO.find { TaskStatusTable.code eq code }
            .firstOrNull()
            ?.let(::statusDaoToModel)
    }

    override suspend fun getAll(): List<TaskStatus> = suspendTransaction {
        TaskStatusDAO.all().map(::statusDaoToModel)
    }
} 