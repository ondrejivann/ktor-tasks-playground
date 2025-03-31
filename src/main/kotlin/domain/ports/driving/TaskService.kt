package domain.ports.driving

import domain.model.command.CreateTaskCommand
import domain.model.Priority
import domain.model.Task

interface TaskService {
    suspend fun allTasks(): List<Task>
    suspend fun tasksByPriority(priority: Priority): List<Task>
    suspend fun taskByName(name: String): Task?
    suspend fun taskById(id: Int): Task?
    suspend fun addTask(command: CreateTaskCommand): Task
    suspend fun updateTaskStatus(name: String, statusCode: String): Boolean
    suspend fun removeTask(name: String): Boolean
}