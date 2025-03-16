package domain.model.command

import domain.model.Priority
import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskCommand(
    val name: String,
    val description: String,
    val priority: Priority
) 