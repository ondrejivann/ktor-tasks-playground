package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Mutation
import infrastructure.graphql.mutations.AdminTestMutations
import org.koin.core.annotation.Single

@Single
class RootMutation(
    private val taskMutations: TaskMutations,
    private val adminTest: AdminTestMutations,
): Mutation {
    fun tasks(): TaskMutations = taskMutations
    fun adminTest(): AdminTestMutations = adminTest
}