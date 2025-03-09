package infrastructure.graphql

import domain.model.Priority
import domain.model.Task
import domain.ports.TaskService
import com.expediagroup.graphql.server.operations.Query
import infrastructure.graphql.model.PriorityGQL
import infrastructure.graphql.model.TaskGQL

class TaskQueries(
    private val taskService: TaskService,
) : Query {
    suspend fun tasks(): List<TaskGQL> = taskService.allTasks().map { it.toGQL() }

    suspend fun task(name: String): TaskGQL {
        val task = taskService.taskByName(name) ?: throw IllegalArgumentException("Task not found: $name")
        return task.toGQL()
    }

    private fun Task.toGQL(): TaskGQL =
        TaskGQL(name, description, priority.toGQL())

    private fun Priority.toGQL(): PriorityGQL =
        when (this) {
            Priority.LOW -> PriorityGQL.LOW
            Priority.MEDIUM -> PriorityGQL.MEDIUM
            Priority.HIGH -> PriorityGQL.HIGH
            Priority.VITAL -> PriorityGQL.VITAL
        }
}