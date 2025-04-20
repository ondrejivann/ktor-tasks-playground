package domain.ports.driven

import domain.model.Priority
import domain.model.Task

interface TaskRepository {
    suspend fun allTasks(): List<Task>

    suspend fun tasksByPriority(priority: Priority): List<Task>

    suspend fun taskByName(name: String): Task?

    suspend fun taskById(id: Int): Task?

    suspend fun addTask(task: Task): Task

    suspend fun updateTask(name: String, task: Task): Boolean

    suspend fun updateTaskStatus(name: String, statusCode: String): Boolean

    suspend fun removeTask(name: String): Boolean
}
