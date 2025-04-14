package infrastructure.persistence.dao

import domain.model.user.User
import infrastructure.persistence.table.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UsersTable)

    var email by UsersTable.email
    var passwordHash by UsersTable.passwordHash
    var firstName by UsersTable.firstName
    var lastName by UsersTable.lastName
    var authProvider by UsersTable.authProvider
    var providerId by UsersTable.providerId
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt

    fun toUser(): User = User(
        id = id.value,
        email = email,
        passwordHash = passwordHash,
        firstName = firstName,
        lastName = lastName,
        authProvider = authProvider,
        providerId = providerId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun User.toDAO(): UserDAO {
    val entity = UserDAO.findById(id) ?: UserDAO.new(id) {
        this.email = this@toDAO.email
        this.passwordHash = this@toDAO.passwordHash
        this.firstName = this@toDAO.firstName
        this.lastName = this@toDAO.lastName
        this.authProvider = this@toDAO.authProvider
        this.providerId = this@toDAO.providerId
        this.createdAt = this@toDAO.createdAt
        this.updatedAt = this@toDAO.updatedAt
    }

    return entity
}