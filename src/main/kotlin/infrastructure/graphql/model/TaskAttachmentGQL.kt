package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import domain.model.TaskAttachment

@GraphQLDescription("Attachment (image or other file)")
data class TaskAttachmentGQL(
    @GraphQLDescription("Id")
    val id: String,
    @GraphQLDescription("Name of the file")
    val fileName: String,
    @GraphQLDescription("Content type of the file, e.g. image/png")
    val contentType: String,
    @GraphQLDescription("Download URL")
    val downloadUrl: String
)

fun TaskAttachment.toGQL(downloadUrl: String): TaskAttachmentGQL {
    return TaskAttachmentGQL(
        id = this.id.toString(),
        fileName = this.fileName,
        contentType = this.contentType,
        downloadUrl = downloadUrl,
    )
}