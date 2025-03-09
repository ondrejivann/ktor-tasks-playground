package domain.model

import kotlinx.serialization.Serializable

enum class Priority {
    LOW, MEDIUM, HIGH, VITAL
}

@Serializable
data class Task(
    val name: String,
    val description: String,
    val priority: Priority
)