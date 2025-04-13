package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Query
import infrastructure.graphql.queries.AdminTestQueries
import infrastructure.graphql.queries.TaskAppQueries
import org.koin.core.annotation.Single

@Single
class RootQuery(
    private val taskAppQueries: TaskAppQueries,
    private val adminTestQueries: AdminTestQueries,
): Query {
    fun taskApp(): TaskAppQueries = taskAppQueries
    fun adminTest(): AdminTestQueries = adminTestQueries
}