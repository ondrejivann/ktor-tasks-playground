package config.ktor

import infrastructure.graphql.graphQLRoutes
import infrastructure.rest.config.configureRestRoutes
import infrastructure.rest.controller.AuthController
import infrastructure.rest.controller.FileController
import infrastructure.rest.controller.TaskAttachmentController
import infrastructure.rest.controller.TaskController
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authController by inject<AuthController>()
    val taskController by inject<TaskController>()
    val fileController by inject<FileController>()
    val taskAttachmentController by inject<TaskAttachmentController>()

    routing {
        graphQLRoutes()
        configureRestRoutes(authController, taskController, fileController, taskAttachmentController)
    }
}
