package domain.model

data class TaskDetail(
    val id: Int,
    val name: String,
    val description: String,
    val priority: Priority,
    val status: TaskStatus,
    val attachments: List<TaskAttachmentDetail>,
)
