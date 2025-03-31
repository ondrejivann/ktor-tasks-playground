package infrastructure.rest.mapper

import domain.model.TaskAttachmentUploadInfo
import infrastructure.rest.dto.PrepareTaskAttachmentUploadResponse

fun TaskAttachmentUploadInfo.toPrepareTaskAttachmentUploadResponse(): PrepareTaskAttachmentUploadResponse =
    PrepareTaskAttachmentUploadResponse(
        id = id,
        uploadUrl = uploadUrl,
        fileKey = fileKey,
        expiresInSeconds = expiresInSeconds,
    )