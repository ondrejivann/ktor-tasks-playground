package infrastructure.graphql.model.files

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Result of a file operation")
data class FileOperationResultGQL(
    val success: Boolean,
    val message: String? = null
)