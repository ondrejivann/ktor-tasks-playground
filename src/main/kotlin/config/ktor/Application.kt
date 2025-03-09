package com.example.config.ktor

import com.example.application.services.TaskServiceImpl
import config.ktor.*
import infrastructure.persistence.TaskRepositoryImpl
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = TaskRepositoryImpl()
    val service = TaskServiceImpl(repository)

    configureCORS()
    configureDatabases()
    configureGraphQL(service)
    configureRouting(service)
    configureStatusPages()
    configureOpenAPI()
}
