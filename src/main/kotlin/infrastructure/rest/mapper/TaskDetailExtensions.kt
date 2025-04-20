package infrastructure.rest.mapper

import domain.model.TaskDetail
import infrastructure.rest.dto.TaskAttachmentResponse
import infrastructure.rest.dto.TaskResponse

fun TaskDetail.toTaskResponse(): TaskResponse = TaskResponse(
    id = id,
    name = name,
    description = description,
    priority = priority,
    status = status,
    attachments =
    attachments.map { attachment ->
        TaskAttachmentResponse(
            id = attachment.id,
            fileName = attachment.fileName,
            contentType = attachment.contentType,
            downloadUrl = attachment.downloadUrl,
        )
    },
)
