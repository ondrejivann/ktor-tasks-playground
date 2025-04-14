package infrastructure.persistence.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokensTable : IntIdTable("refresh_tokens") {
    val userId = reference("user_id", UsersTable)
    val token = varchar("token", 255).uniqueIndex()
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at")
}