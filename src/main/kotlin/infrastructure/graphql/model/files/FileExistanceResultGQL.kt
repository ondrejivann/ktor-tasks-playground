package infrastructure.graphql.model.files

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Result of checking if a file exists")
data class FileExistenceResultGQL(
    val exists: Boolean,
    val fileKey: String,
    val errorMessage: String? = null
)