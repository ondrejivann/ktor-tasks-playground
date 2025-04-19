package infrastructure.persistence.repository

import domain.model.auth.AuthProvider
import domain.model.user.User
import domain.ports.driven.UserRepository
import infrastructure.persistence.dao.UserDAO
import infrastructure.persistence.dao.toDAO
import infrastructure.persistence.table.UsersTable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Single

@Single(binds = [UserRepository::class])
class UserRepositoryImpl : UserRepository {
    private val logger = KotlinLogging.logger {}

    override suspend fun findByEmail(email: String): User? = newSuspendedTransaction {
        logger.debug { "Finding user by email: $email" }
        UserDAO.find { UsersTable.email eq email }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findById(id: Int): User? = newSuspendedTransaction {
        logger.debug { "Finding user by id: $id" }
        UserDAO.findById(id)?.toUser()
    }

    override suspend fun findByProviderId(providerId: String, provider: AuthProvider): User? = newSuspendedTransaction {
        logger.debug { "Finding user by providerId: $providerId, provider: $provider" }
        UserDAO.find {
            (UsersTable.providerId eq providerId) and (UsersTable.authProvider eq provider)
        }.singleOrNull()?.toUser()
    }

    override suspend fun create(user: User): User = newSuspendedTransaction {
        logger.debug { "Creating user: ${user.email}" }
        val entity = user.toDAO()
        entity.toUser()
    }

    override suspend fun update(user: User): User = newSuspendedTransaction {
        logger.debug { "Updating user: ${user.id}" }
        val id = user.id
        val entity = UserDAO.findById(id) ?: throw NoSuchElementException("User not found")

        entity.email = user.email
        entity.passwordHash = user.passwordHash
        entity.firstName = user.firstName
        entity.lastName = user.lastName
        entity.authProvider = user.authProvider
        entity.providerId = user.providerId
        entity.updatedAt = user.updatedAt

        entity.toUser()
    }
}