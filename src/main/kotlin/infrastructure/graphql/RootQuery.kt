package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Query
import org.koin.core.annotation.Single

@Single
class RootQuery(
    private val taskQueries: TaskQueries,
): Query {
    fun tasks(): TaskQueries = taskQueries
}