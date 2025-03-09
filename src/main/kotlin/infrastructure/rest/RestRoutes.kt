package infrastructure.rest

import domain.ports.TaskService
import config.ktor.configureRestSerialization
import io.ktor.server.routing.*

fun Route.configureRestRoutes(
    taskService: TaskService,
) {
    configureRestSerialization()
    taskRoutes(taskService)
}