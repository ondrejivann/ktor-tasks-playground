package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Mutation
import infrastructure.graphql.mutations.AdminTestMutations
import infrastructure.graphql.mutations.TaskAppMutations
import org.koin.core.annotation.Single

@Single
class RootMutation(
    private val taskAppMutations: TaskAppMutations,
    private val adminTest: AdminTestMutations,
): Mutation {
    fun taskApp(): TaskAppMutations = taskAppMutations
    fun adminTest(): AdminTestMutations = adminTest
}