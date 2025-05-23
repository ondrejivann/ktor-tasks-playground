package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val priority: Priority,
    val status: TaskStatus,
    val attachments: List<TaskAttachment>,
)
