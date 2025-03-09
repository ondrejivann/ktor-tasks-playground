package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("GraphQL Task model")
data class TaskGQL(
    val name: String,
    val description: String,
    val priority: PriorityGQL
)