package com.example.config.ktor

import com.example.application.services.TaskServiceImpl
import com.example.infrastructure.persistence.TaskRepositoryImpl
import com.example.infrastructure.rest.configureTaskRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = TaskRepositoryImpl()
    val service = TaskServiceImpl(repository)

    configureTaskRouting(service)
    configureStatusPages()
    configureSerialization()
    configureDatabases()
}
