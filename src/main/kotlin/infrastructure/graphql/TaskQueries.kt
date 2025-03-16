package infrastructure.graphql

import com.expediagroup.graphql.server.operations.Query
import domain.model.Priority
import domain.model.Task
import domain.model.TaskStatus
import domain.ports.TaskService
import infrastructure.graphql.model.PriorityGQL
import infrastructure.graphql.model.TaskGQL
import infrastructure.graphql.model.TaskStatusGQL

class TaskQueries(
    private val taskService: TaskService,
) : Query {
    suspend fun tasks(): List<TaskGQL> = taskService.allTasks().map { it.toGQL() }

    suspend fun task(name: String): TaskGQL {
        val task = taskService.taskByName(name) ?: throw IllegalArgumentException("Task not found: $name")
        return task.toGQL()
    }

    private fun Task.toGQL(): TaskGQL =
        TaskGQL(
            id = id,
            name = name,
            description = description,
            priority = priority.toGQL(),
            status = status.toGQL(),
        )

    private fun Priority.toGQL(): PriorityGQL =
        when (this) {
            Priority.LOW -> PriorityGQL.LOW
            Priority.MEDIUM -> PriorityGQL.MEDIUM
            Priority.HIGH -> PriorityGQL.HIGH
            Priority.VITAL -> PriorityGQL.VITAL
        }

    private fun TaskStatus.toGQL() = TaskStatusGQL(
        id = id,
        code = code,
        name = name,
        description = description,
    )
}