package application.services

import application.exceptions.BusinessRuleViolationException
import common.exceptions.ErrorCodes
import domain.exceptions.EntityNotFoundException
import domain.exceptions.ValidationException
import domain.model.Priority
import domain.model.Task
import domain.model.command.CreateTaskCommand
import domain.ports.driven.TaskRepository
import domain.ports.driving.TaskService
import domain.ports.driving.TaskStatusService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskServiceImpl(private val repository: TaskRepository, private val statusService: TaskStatusService) : TaskService {
    private val logger = KotlinLogging.logger {}

    override suspend fun allTasks(): List<Task> {
        logger.debug { "Fetching all tasks..." }
        return repository.allTasks()
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> = repository.tasksByPriority(priority)

    override suspend fun taskByName(name: String): Task? = repository.taskByName(name)

    override suspend fun taskById(id: Int): Task = repository.taskById(id) ?: throw EntityNotFoundException(
        "Task",
        id,
        errorCode = ErrorCodes.ENTITY_NOT_FOUND,
    )

    override suspend fun addTask(command: CreateTaskCommand): Task {
        logger.debug { "Creating new task: ${command.name}" }

        validateTaskName(command.name)
        validateTaskDescription(command.description)

        val defaultStatus = statusService.getDefaultStatus()

        val task =
            Task(
                id = -1,
                name = command.name.trim(),
                description = command.description.trim(),
                priority = command.priority,
                status = defaultStatus,
                attachments = emptyList(),
            )

        try {
            return repository.addTask(task).also {
                logger.info { "Task '${task.name}' created successfully" }
            }
        } catch (e: IllegalStateException) {
            logger.error { "Failed to create task: ${e.message}" }
            throw BusinessRuleViolationException(
                message = "Failed to create task: ${e.message}",
                cause = e,
                errorCode = ErrorCodes.BUSINESS_RULE_VIOLATION,
            )
        }
    }

    override suspend fun removeTask(name: String): Boolean = repository.removeTask(name)

    override suspend fun updateTaskStatus(name: String, statusCode: String): Boolean {
        logger.debug { "Updating task status: $name to $statusCode" }

        if (name.isBlank()) {
            logger.warn { "Cannot update status: task name is blank" }
            throw ValidationException(
                message = "Task name cannot be empty",
                errorCode = ErrorCodes.VALIDATION_ERROR,
            )
        }

        val status = statusService.getStatusByCode(statusCode)
        if (status == null) {
            logger.warn { "Cannot update status: status code '$statusCode' not found" }
            throw ValidationException(
                message = "Status with code '$statusCode' not found",
                errorCode = ErrorCodes.VALIDATION_ERROR,
            )
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
            throw BusinessRuleViolationException(
                message = "Failed to update task status: ${e.message}",
                cause = e,
                errorCode = ErrorCodes.BUSINESS_RULE_VIOLATION,
            )
        }
    }

    private fun validateTaskName(name: String) {
        if (name.isBlank()) {
            throw ValidationException(
                message = "Task name cannot be empty",
                errorCode = ErrorCodes.VALIDATION_ERROR,
            )
        }
        if (name.length > 128) {
            throw ValidationException(
                message = "Task name cannot be longer than 128 characters",
                errorCode = ErrorCodes.VALIDATION_ERROR,
            )
        }
    }

    private fun validateTaskDescription(description: String) {
        if (description.isBlank()) {
            throw ValidationException(
                message = "Task description cannot be empty",
                errorCode = ErrorCodes.VALIDATION_ERROR,
            )
        }
        if (description.length > 1024) {
            throw ValidationException(
                message = "Task description cannot be longer than 1024 characters",
                errorCode = ErrorCodes.VALIDATION_ERROR,
            )
        }
    }
}
