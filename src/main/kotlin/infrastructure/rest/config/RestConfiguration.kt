package infrastructure.rest.config

import config.ktor.configureRestSerialization
import infrastructure.rest.controller.AuthController
import infrastructure.rest.controller.FileController
import infrastructure.rest.controller.TaskAttachmentController
import infrastructure.rest.controller.TaskController
import infrastructure.rest.route.adminRoutes
import infrastructure.rest.route.authRoutes
import infrastructure.rest.route.fileRoutes
import infrastructure.rest.route.taskAttachmentRoutes
import infrastructure.rest.route.taskRoutes
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route

fun Route.configureRestRoutes(
    authController: AuthController,
    taskController: TaskController,
    fileController: FileController,
    taskAttachmentController: TaskAttachmentController,
) {
    configureRestSerialization()
    authRoutes(authController)
    adminRoutes()
    authenticate("auth-jwt") {
        taskRoutes(taskController)
        fileRoutes(fileController)
        taskAttachmentRoutes(taskAttachmentController)
    }
}
