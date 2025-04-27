package config.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import java.sql.Connection

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var dataSource: HikariDataSource

    fun init(config: DatabaseConfig) {
        try {
            logger.info("Initializing HikariDataSource with URL: ${config.jdbcURL}") // Použij skutečné proměnné
            logger.info("Initializing HikariDataSource with User: ${config.user}")
            // Vytvoření databáze pomocí základního připojení
            createDatabaseIfNotExists(config)

            // Nastavení HikariCP pro aplikační databázi
            val hikariConfig =
                HikariConfig().apply {
                    driverClassName = config.driverClassName
                    jdbcUrl = config.jdbcURL
                    username = config.user
                    password = config.password
                    maximumPoolSize = 3
                    isAutoCommit = false
                    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                    // Volitelné parametry pro lepší výkon
                    addDataSourceProperty("cachePrepStmts", "true")
                    addDataSourceProperty("prepStmtCacheSize", "250")
                    addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
                }

            // Vytvoření DataSource
            dataSource = HikariDataSource(hikariConfig)

            // Inicializace Exposed s naším DataSource
            Database.connect(dataSource)

            // Inicializace a spuštění Flyway migrací
            val flyway =
                Flyway
                    .configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migrations")
                    .baselineOnMigrate(true)
                    .validateMigrationNaming(true)
                    .loggers("slf4j")
                    .load()

            flyway.migrate()
        } catch (e: Exception) {
            logger.error("Failed to initialize database", e)
            throw e
        }
    }

    private fun createDatabaseIfNotExists(config: DatabaseConfig) {
        val defaultHikariConfig =
            HikariConfig().apply {
                driverClassName = config.driverClassName
                jdbcUrl = config.defaultJdbcURL
                username = config.user
                password = config.password
                maximumPoolSize = 1
                isAutoCommit = true
            }

        try {
            HikariDataSource(defaultHikariConfig).use { defaultDataSource ->
                defaultDataSource.connection.use { connection ->
                    if (!databaseExists(connection, config.dbName)) {
                        createDatabase(connection, config.dbName)
                    }
                }
            }
        } catch (e: PSQLException) {
            logger.error("Failed to create database: ${e.message}")
            throw e
        }
    }

    private fun databaseExists(connection: Connection, dbName: String): Boolean {
        connection
            .prepareStatement(
                "SELECT 1 FROM pg_database WHERE datname = ?",
            ).use { statement ->
                statement.setString(1, dbName)
                statement.executeQuery().use { resultSet ->
                    return resultSet.next()
                }
            }
    }

    private fun createDatabase(connection: Connection, dbName: String) {
        connection.createStatement().use { statement ->
            statement.execute("CREATE DATABASE $dbName")
        }
        logger.info("Database $dbName created successfully")
    }

    // fun getDataSource(): HikariDataSource = dataSource
}

data class DatabaseConfig(
    val driverClassName: String,
    val defaultJdbcURL: String,
    val jdbcURL: String,
    val user: String,
    val password: String,
    val dbName: String,
)
