package config.ktor

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
        user = "ktor",
        password = "ktor",
    )
}
