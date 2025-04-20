package infrastructure.rest.dto

import domain.model.Priority
import domain.model.TaskStatus
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Int,
    val name: String,
    val description: String,
    val priority: Priority,
    val status: TaskStatus,
    val attachments: List<TaskAttachmentResponse> = emptyList(),
)
