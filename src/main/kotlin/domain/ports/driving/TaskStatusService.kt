package domain.ports.driving

import domain.model.TaskStatus

interface TaskStatusService {
    suspend fun getDefaultStatus(): TaskStatus

    suspend fun getStatusByCode(code: String): TaskStatus?

    suspend fun getAllStatuses(): List<TaskStatus>
}
