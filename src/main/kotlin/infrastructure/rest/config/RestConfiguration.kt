package infrastructure.rest.config

import config.ktor.configureRestSerialization
import infrastructure.rest.controller.TaskController
import infrastructure.rest.route.taskRoutes
import io.ktor.server.routing.*

fun Route.configureRestRoutes(
    taskController: TaskController,
) {
    configureRestSerialization()
    taskRoutes(taskController)
}