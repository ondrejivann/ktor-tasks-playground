package infrastructure.persistence.table

import domain.model.auth.AuthProvider
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255).nullable()
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
    val authProvider = enumerationByName("auth_provider", 50, AuthProvider::class)
    val providerId = varchar("provider_id", 255).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
