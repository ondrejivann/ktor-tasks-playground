package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("GraphQL Task model")
data class TaskGQL(
    val id: Int,
    val name: String,
    val description: String,
    val priority: PriorityGQL,
    val status: TaskStatusGQL,
    @GraphQLDescription("Seznam příloh úkolu")
    val attachments: List<TaskAttachmentDetailGQL> = emptyList()
)