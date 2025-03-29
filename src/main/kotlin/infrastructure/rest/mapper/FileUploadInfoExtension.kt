package infrastructure.rest.mapper

import domain.model.file.FileUploadInfo
import infrastructure.rest.dto.PrepareTaskAttachmentUploadResponse

fun FileUploadInfo.toPrepareTaskAttachmentUploadResponse(): PrepareTaskAttachmentUploadResponse =
    PrepareTaskAttachmentUploadResponse(
        uploadUrl = uploadUrl,
        fileKey = fileKey,
        expiresInSeconds = expiresInSeconds,
    )