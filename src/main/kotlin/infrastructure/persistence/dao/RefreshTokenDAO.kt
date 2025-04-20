package infrastructure.persistence.dao

import domain.model.auth.RefreshToken
import infrastructure.persistence.table.RefreshTokensTable
import infrastructure.persistence.table.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RefreshTokenDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RefreshTokenDAO>(RefreshTokensTable)

    var userId by RefreshTokensTable.userId
    var token by RefreshTokensTable.token
    var expiresAt by RefreshTokensTable.expiresAt
    var createdAt by RefreshTokensTable.createdAt

    fun toRefreshToken(): RefreshToken = RefreshToken(
        id = id.value,
        userId = userId.value,
        token = token,
        expiresAt = expiresAt,
        createdAt = createdAt,
    )
}

fun RefreshToken.toEntity(): RefreshTokenDAO = RefreshTokenDAO.findById(id) ?: RefreshTokenDAO.new {
    this.userId = EntityID(this@toEntity.userId, UsersTable)
    this.token = this@toEntity.token
    this.expiresAt = this@toEntity.expiresAt
    this.createdAt = this@toEntity.createdAt
}
