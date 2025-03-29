package application.services

import domain.model.TaskStatus
import domain.ports.driven.TaskStatusRepository
import domain.ports.driving.TaskStatusService
import org.koin.core.annotation.Single

@Single
class TaskStatusServiceImpl(
    private val repository: TaskStatusRepository
) : TaskStatusService {
    
    override suspend fun getDefaultStatus(): TaskStatus {
        return repository.getByCode(DEFAULT_STATUS_CODE)
            ?: throw IllegalStateException("Default status '$DEFAULT_STATUS_CODE' not found")
    }

    override suspend fun getStatusByCode(code: String): TaskStatus? {
        return repository.getByCode(code)
    }

    override suspend fun getAllStatuses(): List<TaskStatus> {
        return repository.getAll()
    }

    companion object {
        private const val DEFAULT_STATUS_CODE = "pending"
    }
} 