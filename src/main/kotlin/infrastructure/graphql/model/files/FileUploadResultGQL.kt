package infrastructure.graphql.model.files

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Result of preparing a file upload")
data class FileUploadResultGQL(
    val success: Boolean,
    val uploadUrl: String? = null,
    val fileKey: String? = null,
    val expiresInSeconds: Int? = null,
    val message: String? = null
)