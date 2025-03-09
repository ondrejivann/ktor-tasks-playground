package config.ktor

import domain.ports.TaskService
import infrastructure.graphql.graphQLRoutes
import infrastructure.rest.configureRestRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    taskService: TaskService,
) {
    routing {
        graphQLRoutes()
        configureRestRoutes(taskService)
    }
}