package infrastructure.graphql

import application.services.TaskCreationException
import com.expediagroup.graphql.server.operations.Mutation
import domain.model.Priority
import domain.model.command.CreateTaskCommand
import domain.ports.TaskService
import infrastructure.graphql.model.CreateTaskCommandGQL
import infrastructure.graphql.model.PriorityGQL
import infrastructure.graphql.model.TaskOperationResultGQL
import infrastructure.graphql.model.TaskStatusUpdateResultGQL
import org.koin.core.annotation.Single

@Single
class TaskMutations(
    private val taskService: TaskService,
) {
    suspend fun addTask(input: CreateTaskCommandGQL): TaskOperationResultGQL {
        return try {
            taskService.addTask(input.toDomain())
            TaskOperationResultGQL(
                success = true,
                message = "Task '${input.name}' successfully created"
            )
        } catch (e: TaskCreationException) {
            TaskOperationResultGQL(
                success = false,
                message = e.message ?: "Failed to create task due to business rules"
            )
        } catch (e: IllegalArgumentException) {
            TaskOperationResultGQL(
                success = false,
                message = "Invalid input: ${e.message}"
            )
        } catch (e: Exception) {
            TaskOperationResultGQL(
                success = false,
                message = "Unexpected error while creating task: ${e.message}"
            )
        }
    }

    suspend fun updateTaskStatus(name: String, statusCode: String): TaskStatusUpdateResultGQL {
        return try {
            val success = taskService.updateTaskStatus(name, statusCode)
            if (success) {
                TaskStatusUpdateResultGQL(
                    success = true,
                    message = "Task status updated successfully"
                )
            } else {
                TaskStatusUpdateResultGQL(
                    success = false,
                    message = "Task or status not found"
                )
            }
        } catch (e: IllegalArgumentException) {
            TaskStatusUpdateResultGQL(
                success = false,
                message = "Invalid input: ${e.message}"
            )
        } catch (e: Exception) {
            TaskStatusUpdateResultGQL(
                success = false,
                message = "Failed to update task status: ${e.message}"
            )
        }
    }

    suspend fun removeTask(name: String): TaskOperationResultGQL {
        return try {
            val success = taskService.removeTask(name)
            if (success) {
                TaskOperationResultGQL(
                    success = true,
                    message = "Task successfully removed"
                )
            } else {
                TaskOperationResultGQL(
                    success = false,
                    message = "Task not found"
                )
            }
        } catch (e: IllegalArgumentException) {
            TaskOperationResultGQL(
                success = false,
                message = "Invalid input: ${e.message}"
            )
        } catch (e: Exception) {
            TaskOperationResultGQL(
                success = false,
                message = "Failed to remove task: ${e.message}"
            )
        }
    }

    private fun PriorityGQL.toDomain(): Priority =
        when (this) {
            PriorityGQL.LOW -> Priority.LOW
            PriorityGQL.MEDIUM -> Priority.MEDIUM
            PriorityGQL.HIGH -> Priority.HIGH
            PriorityGQL.VITAL -> Priority.VITAL
        }

    private fun CreateTaskCommandGQL.toDomain() = CreateTaskCommand(
        name = name,
        description = description,
        priority = priority,
    )
}