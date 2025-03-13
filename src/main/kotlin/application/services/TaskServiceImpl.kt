package application.services

import domain.model.Priority
import domain.model.Task
import domain.ports.TaskRepository
import domain.ports.TaskService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

@Single
class TaskServiceImpl(
    private val repository: TaskRepository
): TaskService {

    private val logger = KotlinLogging.logger {}

    override suspend fun allTasks(): List<Task> {
        logger.debug { "Fetching all tasks..." }
        return repository.allTasks()
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> {
        return repository.tasksByPriority(priority)
    }

    override suspend fun taskByName(name: String): Task? {
        return repository.taskByName(name)
    }

    override suspend fun addTask(task: Task) {
        return repository.addTask(task)
    }

    override suspend fun removeTask(name: String): Boolean {
        return repository.removeTask(name)
    }
}