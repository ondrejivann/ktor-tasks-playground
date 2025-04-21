package domain.ports.driving

import domain.filter.TaskFilter
import domain.model.Priority
import domain.model.TaskDetail
import domain.model.command.CreateTaskCommand
import domain.model.command.CreateTaskCommandResponse

interface TaskDetailService {
    suspend fun getAllTasks(filter: TaskFilter? = null): List<TaskDetail>

    suspend fun getTaskDetailByPriority(priority: Priority): List<TaskDetail>

    suspend fun getTaskByName(name: String): TaskDetail?

    suspend fun getTaskById(id: Int): TaskDetail?

    suspend fun addTask(command: CreateTaskCommand): CreateTaskCommandResponse

    suspend fun removeTask(name: String): Boolean

    suspend fun updateTaskStatus(name: String, statusCode: String): Boolean
}
