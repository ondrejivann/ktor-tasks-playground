package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Mutation
import org.koin.core.annotation.Single

@Single
class RootMutation(
    private val taskMutations: TaskMutations,
): Mutation {
    fun tasks(): TaskMutations = taskMutations
}