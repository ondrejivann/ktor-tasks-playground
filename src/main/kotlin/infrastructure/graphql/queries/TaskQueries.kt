package infrastructure.graphql.queries

import domain.filter.SortDirection
import domain.filter.TaskFilter
import domain.filter.TaskSortField
import domain.model.Priority
import domain.model.TaskStatus
import domain.ports.driving.TaskDetailService
import infrastructure.graphql.auth.directive.RequireAuth
import infrastructure.graphql.model.*
import org.koin.core.annotation.Single

@Single
class TaskQueries(
    private val taskDetailService: TaskDetailService,
) {
    suspend fun all(filter: TaskFilterGQL? = null): List<TaskGQL> {
        val domainFilter = filter?.toDomain()
        return taskDetailService.getAllTasks(domainFilter).map { it.toGQL() }
    }

    suspend fun taskById(id: Int): TaskGQL? {
        return taskDetailService.getTaskById(id)?.toGQL()
    }

    suspend fun taskByName(name: String): TaskGQL? {
        return taskDetailService.getTaskByName(name)?.toGQL()
    }
}

fun Priority.toGQL(): PriorityGQL =
    when (this) {
        Priority.LOW -> PriorityGQL.LOW
        Priority.MEDIUM -> PriorityGQL.MEDIUM
        Priority.HIGH -> PriorityGQL.HIGH
        Priority.VITAL -> PriorityGQL.VITAL
    }

fun PriorityGQL.toDomain(): Priority =
    when(this) {
        PriorityGQL.LOW -> Priority.LOW
        PriorityGQL.MEDIUM -> Priority.MEDIUM
        PriorityGQL.HIGH -> Priority.HIGH
        PriorityGQL.VITAL -> Priority.VITAL
    }

fun TaskStatus.toGQL() = TaskStatusGQL(
    id = id,
    code = code,
    name = name,
    description = description,
)

fun domain.model.TaskDetail.toGQL(): TaskGQL =
    TaskGQL(
        id = id,
        name = name,
        description = description,
        priority = priority.toGQL(),
        status = status.toGQL(),
        attachments = attachments.map { it.toGQL() }
    )

fun TaskFilterGQL.toDomain(): TaskFilter {
    return TaskFilter(
        priority = priority?.toDomain(),
        statusCode = statusCode,
        nameContains = nameContains,
        descriptionContains = descriptionContains,
        searchText = searchText,
        sortBy = sortBy?.toDomain(),
        sortDirection = sortDirection?.toDomain()
    )
}

fun TaskSortFieldGQL.toDomain(): TaskSortField =
    when (this) {
        TaskSortFieldGQL.NAME -> TaskSortField.NAME
        TaskSortFieldGQL.PRIORITY -> TaskSortField.PRIORITY
        TaskSortFieldGQL.STATUS -> TaskSortField.STATUS
    }

fun SortDirectionGQL.toDomain(): SortDirection =
    when (this) {
        SortDirectionGQL.ASC -> SortDirection.ASC
        SortDirectionGQL.DESC -> SortDirection.DESC
    }