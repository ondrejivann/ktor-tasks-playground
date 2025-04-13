package infrastructure.rest.config

import config.ktor.configureRestSerialization
import infrastructure.rest.controller.FileController
import infrastructure.rest.controller.TaskAttachmentController
import infrastructure.rest.controller.TaskController
import infrastructure.rest.route.fileRoutes
import infrastructure.rest.route.taskAttachmentRoutes
import infrastructure.rest.route.taskRoutes
import io.ktor.server.routing.*

fun Route.configureRestRoutes(
    taskController: TaskController,
    fileController: FileController,
    taskAttachmentController: TaskAttachmentController,
) {
    configureRestSerialization()
    taskRoutes(taskController)
    fileRoutes(fileController)
    taskAttachmentRoutes(taskAttachmentController)
}