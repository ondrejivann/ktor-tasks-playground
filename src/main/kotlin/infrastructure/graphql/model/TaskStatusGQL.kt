package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("GraphQL Task status model")
data class TaskStatusGQL(val id: Int, val code: String, val name: String, val description: String?)
