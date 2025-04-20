package infrastructure.graphql.model.files

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Result of generating a file download link")
data class FileDownloadResultGQL(
    val success: Boolean,
    val downloadUrl: String? = null,
    val fileName: String? = null,
    val expiresInSeconds: Int? = null,
    val message: String? = null,
)
