package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Query
import infrastructure.graphql.queries.AdminTestQueries
import org.koin.core.annotation.Single

@Single
class RootQuery(
    private val taskQueries: TaskQueries,
    private val adminTestQueries: AdminTestQueries,
): Query {
    fun tasks(): TaskQueries = taskQueries
    fun adminTest(): AdminTestQueries = adminTestQueries
}