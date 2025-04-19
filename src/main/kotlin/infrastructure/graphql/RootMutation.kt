package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Mutation
import infrastructure.graphql.auth.directive.RequireAuth
import infrastructure.graphql.mutations.AdminTestMutations
import infrastructure.graphql.mutations.AuthMutations
import infrastructure.graphql.mutations.TaskAppMutations
import org.koin.core.annotation.Single

@Single
class RootMutation(
    private val taskAppMutations: TaskAppMutations,
    private val adminTest: AdminTestMutations,
    private val authMutations: AuthMutations,
): Mutation {
    @RequireAuth
    fun taskApp(): TaskAppMutations = taskAppMutations
    @RequireAuth
    fun adminTest(): AdminTestMutations = adminTest
    fun auth(): AuthMutations = authMutations
}