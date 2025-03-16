package domain.ports

import domain.model.command.CreateTaskCommand
import domain.model.Priority
import domain.model.Task

interface TaskService {
    suspend fun allTasks(): List<Task>
    suspend fun tasksByPriority(priority: Priority): List<Task>
    suspend fun taskByName(name: String): Task?
    suspend fun addTask(command: CreateTaskCommand)
    suspend fun updateTaskStatus(name: String, statusCode: String): Boolean
    suspend fun removeTask(name: String): Boolean
}