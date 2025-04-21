package domain.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TaskStatus(
    @Contextual val id: Int,
    val code: String,
    val name: String,
    val description: String? = null,
    val color: String? = null,
    val icon: String? = null,
    val displayOrder: Int,
)
