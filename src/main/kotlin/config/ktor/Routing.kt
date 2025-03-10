package config.ktor

import domain.ports.TaskService
import infrastructure.graphql.graphQLRoutes
import infrastructure.rest.configureRestRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val taskService by inject<TaskService>()

    routing {
        graphQLRoutes()
        configureRestRoutes(taskService)
    }
}