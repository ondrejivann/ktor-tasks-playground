package application.services

import domain.model.Priority
import domain.model.Task
import domain.model.command.CreateTaskCommand
import domain.ports.TaskRepository
import domain.ports.TaskService
import domain.ports.TaskStatusService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val statusService: TaskStatusService,
): TaskService {
    private val logger = KotlinLogging.logger {}

    override suspend fun allTasks(): List<Task> {
        logger.debug { "Fetching all tasks..." }
        return repository.allTasks()
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> {
        return repository.tasksByPriority(priority)
    }

    override suspend fun taskByName(name: String): Task? {
        return repository.taskByName(name)
    }

    override suspend fun addTask(command: CreateTaskCommand) {
        logger.debug { "Creating new task: ${command.name}" }

        validateTaskName(command.name)
        validateTaskDescription(command.description)

        val defaultStatus = statusService.getDefaultStatus()
        
        val task = Task(
            id = -1,
            name = command.name.trim(),
            description = command.description.trim(),
            priority = command.priority,
            status = defaultStatus,
        )

        try {
            repository.addTask(task)
            logger.info { "Task '${task.name}' created successfully" }
        } catch (e: IllegalStateException) {
            logger.error { "Failed to create task: ${e.message}" }
            throw TaskCreationException("Failed to create task: ${e.message}", e)
        }
    }

    override suspend fun removeTask(name: String): Boolean {
        return repository.removeTask(name)
    }

    override suspend fun updateTaskStatus(name: String, statusCode: String): Boolean {
        logger.debug { "Updating task status: $name to $statusCode" }

        if (name.isBlank()) {
            logger.warn { "Cannot update status: task name is blank" }
            throw IllegalArgumentException("Task name cannot be empty")
        }

        val status = statusService.getStatusByCode(statusCode)
        if (status == null) {
            logger.warn { "Cannot update status: status code '$statusCode' not found" }
            throw IllegalArgumentException("Status with code '$statusCode' not found")
        }

        try {
            val result = repository.updateTaskStatus(name, statusCode)
            if (result) {
                logger.info { "Task '$name' status updated to '$statusCode' successfully" }
            } else {
                logger.warn { "Task '$name' not found for status update" }
            }
            return result
        } catch (e: Exception) {
            logger.error(e) { "Failed to update task status: ${e.message}" }
            throw TaskUpdateException("Failed to update task status: ${e.message}", e)
        }
    }

    private fun validateTaskName(name: String) {
        if (name.isBlank()) {
            throw TaskValidationException("Task name cannot be empty")
        }
        if (name.length > 128) {
            throw TaskValidationException("Task name cannot be longer than 128 characters")
        }
    }

    private fun validateTaskDescription(description: String) {
        if (description.isBlank()) {
            throw TaskValidationException("Task description cannot be empty")
        }
        if (description.length > 1024) {
            throw TaskValidationException("Task description cannot be longer than 1024 characters")
        }
    }
}

class TaskValidationException(message: String) : IllegalArgumentException(message)
class TaskCreationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
class TaskUpdateException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)