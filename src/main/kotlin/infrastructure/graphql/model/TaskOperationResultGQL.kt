package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Result of a task operation")
data class TaskOperationResultGQL(val success: Boolean, val message: String)
