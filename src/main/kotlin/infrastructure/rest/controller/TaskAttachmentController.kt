package infrastructure.rest.controller

import io.ktor.server.application.ApplicationCall

interface TaskAttachmentController {
    suspend fun getAttachmentsForTask(call: ApplicationCall)

    suspend fun prepareAttachmentUpload(call: ApplicationCall)

    suspend fun confirmAttachmentUpload(call: ApplicationCall)

    suspend fun removeAttachment(call: ApplicationCall)

    suspend fun getAttachmentDownloadUrl(call: ApplicationCall)
}
