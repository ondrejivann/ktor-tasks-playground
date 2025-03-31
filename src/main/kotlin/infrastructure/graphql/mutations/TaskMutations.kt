package infrastructure.graphql.mutations

import domain.model.command.CreateTaskCommand
import domain.model.command.CreateTaskCommandResponse
import domain.ports.driving.TaskDetailService
import infrastructure.graphql.model.*
import infrastructure.graphql.queries.toGQL
import infrastructure.rest.dto.PrepareTaskAttachmentUploadRequest
import infrastructure.rest.dto.PrepareTaskAttachmentUploadResponse
import org.koin.core.annotation.Single

@Single
class TaskMutations(
    private val taskDetailService: TaskDetailService,
) {
    suspend fun addTask(input: CreateTaskCommandGQL): CreateTaskCommandResponseGQL {
        val addTaskCommandResponse = taskDetailService.addTask(input.toDomain())
        return addTaskCommandResponse.toGQL()
    }

    suspend fun updateTaskStatus(name: String, statusCode: String): TaskStatusUpdateResultGQL {
        return try {
            val success = taskDetailService.updateTaskStatus(name, statusCode)
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
            val success = taskDetailService.removeTask(name)
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

    private fun CreateTaskCommandGQL.toDomain() = CreateTaskCommand(
        name = name,
        description = description,
        priority = priority,
        attachments = attachments.map { it.toPrepareTaskAttachmentUploadRequest() }
    )

    private fun PrepareTaskAttachmentUploadInputGQL.toPrepareTaskAttachmentUploadRequest() =
        PrepareTaskAttachmentUploadRequest(
            fileName = fileName,
            contentType = contentType,
            fileSize = fileSize,
        )
}

data class CreateTaskCommandResponseGQL(
    val id: String,
    val name: String,
    val description: String,
    val priority: PriorityGQL,
    val status: TaskStatusGQL,
    val attachments: List<TaskAttachmentUploadInfoGQL> = emptyList(),
)

data class TaskAttachmentUploadInfoGQL(
    val id: Int,
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Int,
)

fun PrepareTaskAttachmentUploadResponse.toGQL() = TaskAttachmentUploadInfoGQL(
    id = id,
    uploadUrl = uploadUrl,
    fileKey = fileKey,
    expiresInSeconds = expiresInSeconds,
)

fun CreateTaskCommandResponse.toGQL(): CreateTaskCommandResponseGQL =
    CreateTaskCommandResponseGQL(
        id = id.toString(),
        name = name,
        description = description,
        priority = priority.toGQL(),
        status = status.toGQL(),
        attachments = attachments.map {
            it.toGQL()
        }
    )