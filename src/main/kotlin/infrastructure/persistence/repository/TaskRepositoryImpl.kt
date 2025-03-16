package infrastructure.persistence.repository

import domain.model.Priority
import domain.model.Task
import domain.ports.TaskRepository
import infrastructure.persistence.dao.TaskDAO
import infrastructure.persistence.dao.TaskStatusDAO
import infrastructure.persistence.mappers.taskDaoToModelWithStatus
import infrastructure.persistence.common.suspendTransaction
import infrastructure.persistence.table.TaskStatusTable
import infrastructure.persistence.table.TaskTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single
class TaskRepositoryImpl : TaskRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO
            .all()
            .map(::taskDaoToModelWithStatus)
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> = suspendTransaction {
        TaskDAO
            .find { TaskTable.priority eq priority }
            .map(::taskDaoToModelWithStatus)
    }

    override suspend fun taskByName(name: String): Task? = suspendTransaction {
        TaskDAO
            .find { (TaskTable.name eq name) }
            .limit(1)
            .map(::taskDaoToModelWithStatus)
            .firstOrNull()
    }

    override suspend fun addTask(task: Task): Unit = suspendTransaction {
        TaskDAO.new {
            name = task.name
            description = task.description
            priority = task.priority
            statusId = EntityID(task.status.id, TaskStatusTable)
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
    }

    override suspend fun updateTask(name: String, task: Task): Boolean = suspendTransaction {
        val existingTask = TaskDAO.find { TaskTable.name eq name }.firstOrNull()
        if (existingTask != null) {
            existingTask.apply {
                this.name = task.name
                this.description = task.description
                this.priority = task.priority
                this.updatedAt = LocalDateTime.now()
            }
            true
        } else false
    }

    override suspend fun updateTaskStatus(name: String, statusCode: String): Boolean = suspendTransaction {
        val task = TaskDAO.find { TaskTable.name eq name }.firstOrNull()
        val newStatus = TaskStatusDAO.find { TaskStatusTable.code eq statusCode }.firstOrNull()

        if (task != null && newStatus != null) {
            task.apply {
                status = newStatus
                updatedAt = LocalDateTime.now()
            }
            true
        } else false
    }

    override suspend fun removeTask(name: String): Boolean = suspendTransaction {
        val rowsDeleted = TaskTable.deleteWhere {
            TaskTable.name eq name
        }
        rowsDeleted == 1
    }
}