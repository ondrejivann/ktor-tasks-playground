package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Query
import infrastructure.graphql.auth.directive.RequireAuth
import infrastructure.graphql.queries.AdminTestQueries
import infrastructure.graphql.queries.AuthQueries
import infrastructure.graphql.queries.TaskAppQueries
import org.koin.core.annotation.Single

@Single
class RootQuery(
    private val taskAppQueries: TaskAppQueries,
    private val adminTestQueries: AdminTestQueries,
    private val authQueries: AuthQueries,
): Query {
    @RequireAuth
    fun taskApp(): TaskAppQueries = taskAppQueries
    @RequireAuth
    fun adminTest(): AdminTestQueries = adminTestQueries
    @RequireAuth
    fun auth(): AuthQueries = authQueries
}