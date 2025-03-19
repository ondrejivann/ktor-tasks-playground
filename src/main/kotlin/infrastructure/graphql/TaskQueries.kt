package infrastructure.graphql

import domain.model.Priority
import domain.model.Task
import domain.model.TaskStatus
import domain.ports.TaskService
import infrastructure.graphql.model.*
import org.koin.core.annotation.Single

@Single
class TaskQueries(
    private val taskService: TaskService,
) {

    // Filtrování úkolů by mělo být pro lepší efektivitu implementováno přímo v service nebo v repozitáři blíže databázi
    suspend fun all(filter: TaskFilterGQL? = null): List<TaskGQL> {
        if (filter == null) {
            return taskService.allTasks().map { it.toGQL() }
        }

        var filteredTasks = taskService.allTasks()

        if (filter.priority != null) {
            filteredTasks = filteredTasks.filter {
                it.priority.toGQL() == filter.priority
            }
        }

        if (filter.statusCode != null) {
            filteredTasks = filteredTasks.filter {
                it.status.code == filter.statusCode
            }
        }

        if (filter.nameContains != null) {
            filteredTasks = filteredTasks.filter {
                it.name.contains(filter.nameContains, ignoreCase = true)
            }
        }

        if (filter.descriptionContains != null) {
            filteredTasks = filteredTasks.filter {
                it.description.contains(filter.descriptionContains, ignoreCase = true)
            }
        }

        if (filter.searchText != null) {
            filteredTasks = filteredTasks.filter { task ->
                task.name.contains(filter.searchText, ignoreCase = true) ||
                        task.description.contains(filter.searchText, ignoreCase = true)
            }
        }

        if (filter.sortBy != null) {
            filteredTasks = when (filter.sortBy) {
                TaskSortFieldGQL.NAME -> {
                    if (filter.sortDirection == SortDirectionGQL.ASC)
                        filteredTasks.sortedBy { it.name }
                    else
                        filteredTasks.sortedByDescending { it.name }
                }
                TaskSortFieldGQL.PRIORITY -> {
                    if (filter.sortDirection == SortDirectionGQL.ASC)
                        filteredTasks.sortedBy { it.priority.ordinal }
                    else
                        filteredTasks.sortedByDescending { it.priority.ordinal }
                }
                TaskSortFieldGQL.STATUS -> {
                    if (filter.sortDirection == SortDirectionGQL.ASC)
                        filteredTasks.sortedBy { it.status.code }
                    else
                        filteredTasks.sortedByDescending { it.status.code }
                }
            }
        }

        return filteredTasks.map { it.toGQL() }
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

    private fun PriorityGQL.toDomain(): Priority =
        when(this) {
            PriorityGQL.LOW -> Priority.LOW
            PriorityGQL.MEDIUM -> Priority.MEDIUM
            PriorityGQL.HIGH -> Priority.HIGH
            PriorityGQL.VITAL -> Priority.VITAL
        }

    private fun TaskStatus.toGQL() = TaskStatusGQL(
        id = id,
        code = code,
        name = name,
        description = description,
    )
}