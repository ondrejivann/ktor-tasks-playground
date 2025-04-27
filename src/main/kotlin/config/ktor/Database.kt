package config.ktor

import config.db.DatabaseConfig
import config.db.DatabaseFactory
import io.ktor.server.application.Application

fun Application.configureDatabase() {
    val config = environment.config

    val dbConfig =
        DatabaseConfig(
            driverClassName = config.property("database.driverClassName").getString(),
            defaultJdbcURL = config.property("database.defaultJdbcURL").getString(),
            jdbcURL = config.property("database.jdbcURL").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
            dbName = config.property("database.dbName").getString(),
        )

    DatabaseFactory.init(dbConfig)
}
