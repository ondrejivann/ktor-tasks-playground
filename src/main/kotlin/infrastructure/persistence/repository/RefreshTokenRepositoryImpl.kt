package infrastructure.persistence.repository

import domain.model.auth.RefreshToken
import domain.ports.driven.RefreshTokenRepository
import infrastructure.persistence.dao.RefreshTokenDAO
import infrastructure.persistence.dao.toEntity
import infrastructure.persistence.table.RefreshTokensTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Single
import java.time.LocalDateTime

/**
 * Implementation of RefreshTokenRepository interface that interacts with the database
 * using Exposed ORM framework.
 */
@Single(binds = [RefreshTokenRepository::class])
class RefreshTokenRepositoryImpl : RefreshTokenRepository {
    override suspend fun save(refreshToken: RefreshToken): RefreshToken = newSuspendedTransaction {
        refreshToken.toEntity().toRefreshToken()
    }

    override suspend fun findByToken(token: String): RefreshToken? = newSuspendedTransaction {
        RefreshTokenDAO.find { RefreshTokensTable.token eq token }
            .singleOrNull()
            ?.toRefreshToken()
    }

    override suspend fun invalidateAllForUser(userId: Int): Int = newSuspendedTransaction {
        RefreshTokensTable.deleteWhere { RefreshTokensTable.userId eq userId }
    }

    override suspend fun delete(id: Int): Boolean = newSuspendedTransaction {
        val entity = RefreshTokenDAO.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    override suspend fun deleteExpired(): Int = newSuspendedTransaction {
        val now = LocalDateTime.now()
        RefreshTokensTable.deleteWhere { expiresAt less now }
    }
}
