package domain.ports

import domain.model.TaskStatus

interface TaskStatusRepository {
    suspend fun getById(id: Int): TaskStatus?
    suspend fun getByCode(code: String): TaskStatus?
    suspend fun getAll(): List<TaskStatus>
} 