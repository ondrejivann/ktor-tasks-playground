package domain.model.command

import domain.model.Priority
import domain.model.TaskStatus
import infrastructure.rest.dto.PrepareTaskAttachmentUploadRequest
import infrastructure.rest.dto.PrepareTaskAttachmentUploadResponse
import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskCommand(
    val name: String,
    val description: String,
    val priority: Priority,
    val attachments: List<PrepareTaskAttachmentUploadRequest> = emptyList(),
)

@Serializable
data class CreateTaskCommandResponse(
    val id: Int,
    val name: String,
    val description: String,
    val priority: Priority,
    val status: TaskStatus,
    val attachments: List<PrepareTaskAttachmentUploadResponse> = emptyList(),
)