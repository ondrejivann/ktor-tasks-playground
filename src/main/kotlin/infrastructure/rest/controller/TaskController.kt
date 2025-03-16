package infrastructure.rest.controller

import io.ktor.server.application.*

interface TaskController {
    suspend fun getAllTasks(call: ApplicationCall)
    suspend fun getTaskByName(call: ApplicationCall)
    suspend fun getTasksByPriority(call: ApplicationCall)
    suspend fun createTask(call: ApplicationCall)
    suspend fun updateTaskStatus(call: ApplicationCall)
    suspend fun deleteTask(call: ApplicationCall)
}