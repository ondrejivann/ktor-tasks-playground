package infrastructure.graphql

import domain.model.Priority
import domain.model.Task
import domain.ports.TaskService
import com.expediagroup.graphql.server.operations.Mutation
import infrastructure.graphql.model.PriorityGQL
import infrastructure.graphql.model.TaskGQL

class TaskMutations(
    private val taskService: TaskService,
) : Mutation {
    suspend fun addTask(input: TaskGQL): TaskGQL {
        val newTask = Task(
            name = input.name,
            description = input.description,
            priority = input.priority.toDomain()
        )
        taskService.addTask(newTask)
        return input
    }

    private fun PriorityGQL.toDomain(): Priority =
        when (this) {
            PriorityGQL.LOW -> Priority.LOW
            PriorityGQL.MEDIUM -> Priority.MEDIUM
            PriorityGQL.HIGH -> Priority.HIGH
            PriorityGQL.VITAL -> Priority.VITAL
        }
}