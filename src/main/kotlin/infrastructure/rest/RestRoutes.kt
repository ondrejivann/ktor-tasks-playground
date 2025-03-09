package infrastructure.rest

import config.ktor.configureRestSerialization
import domain.ports.TaskService
import io.ktor.server.routing.*

fun Route.configureRestRoutes(
    taskService: TaskService,
) {
    configureRestSerialization()
    taskRoutes(taskService)
}