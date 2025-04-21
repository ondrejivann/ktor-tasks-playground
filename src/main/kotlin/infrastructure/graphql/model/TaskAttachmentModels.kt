package infrastructure.graphql.model

import com.expediagroup.graphql.generator.scalars.ID
import domain.model.TaskAttachmentDetail
import domain.model.TaskAttachmentUploadInfo

data class PrepareTaskAttachmentUploadRequestGQL(val fileName: String, val contentType: String, val fileSize: Int)

data class PrepareTaskAttachmentUploadResponseGQL(
    val id: Int,
    val uploadUrl: String,
    val fileKey: String,
    val expiresInSeconds: Int,
    val success: Boolean = true,
    val errorMessage: String? = null,
)

data class ConfirmTaskAttachmentUploadRequestGQL(val fileKey: String)

data class TaskAttachmentOperationResultGQL(val success: Boolean, val message: String, val attachmentId: Int? = null)

data class TaskAttachmentDownloadResponseGQL(
    val downloadUrl: String,
    val success: Boolean = true,
    val errorMessage: String? = null,
)

fun TaskAttachmentDetail.toGQL(): TaskAttachmentDetailGQL = TaskAttachmentDetailGQL(
    id = ID(id.toString()),
    fileName = fileName,
    contentType = contentType,
    downloadUrl = downloadUrl,
)

fun TaskAttachmentUploadInfo.toGQL(): PrepareTaskAttachmentUploadResponseGQL = PrepareTaskAttachmentUploadResponseGQL(
    id = id,
    uploadUrl = uploadUrl,
    fileKey = fileKey,
    expiresInSeconds = expiresInSeconds,
)

fun createAttachmentErrorResponse(message: String): TaskAttachmentOperationResultGQL = TaskAttachmentOperationResultGQL(
    success = false,
    message = message,
)

fun createDownloadUrlErrorResponse(message: String): TaskAttachmentDownloadResponseGQL = TaskAttachmentDownloadResponseGQL(
    downloadUrl = "",
    success = false,
    errorMessage = message,
)

fun createPrepareUploadErrorResponse(message: String): PrepareTaskAttachmentUploadResponseGQL =
    PrepareTaskAttachmentUploadResponseGQL(
        id = -1,
        uploadUrl = "",
        fileKey = "",
        expiresInSeconds = 0,
        success = false,
        errorMessage = message,
    )
