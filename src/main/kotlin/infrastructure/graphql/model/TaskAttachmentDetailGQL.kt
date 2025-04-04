package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID

@GraphQLDescription("Attachment (image or other file)")
data class TaskAttachmentDetailGQL(
    @GraphQLDescription("Id")
    val id: ID,
    @GraphQLDescription("Name of the file")
    val fileName: String,
    @GraphQLDescription("Content type of the file, e.g. image/png")
    val contentType: String,
    @GraphQLDescription("Download URL")
    val downloadUrl: String
)