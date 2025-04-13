package infrastructure.rest.route

import infrastructure.rest.controller.TaskAttachmentController
import io.ktor.server.routing.*

fun Route.taskAttachmentRoutes(taskAttachmentController: TaskAttachmentController) {
    route("/tasks/{taskId}/attachments") {
        get {
            taskAttachmentController.getAttachmentsForTask(call)
        }

        post("/prepare-upload") {
            taskAttachmentController.prepareAttachmentUpload(call)
        }

        post("/confirm-upload") {
            taskAttachmentController.confirmAttachmentUpload(call)
        }
    }

    route("/attachments/{attachmentId}") {
        delete {
            taskAttachmentController.removeAttachment(call)
        }

        get("/download-url") {
            taskAttachmentController.getAttachmentDownloadUrl(call)
        }
    }
}