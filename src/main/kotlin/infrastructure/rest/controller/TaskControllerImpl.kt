package infrastructure.rest.controller

import domain.model.Priority
import domain.model.command.CreateTaskCommand
import domain.ports.TaskService
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.annotation.Single

@Single(binds = [TaskController::class])
class TaskControllerImpl(
    private val taskService: TaskService,
): TaskController {

    override suspend fun getAllTasks(call: ApplicationCall) {
        val tasks = taskService.allTasks()
        call.respond(tasks)
    }

    override suspend fun getTaskByName(call: ApplicationCall) {
        val name = call.parameters["taskName"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val task = taskService.taskByName(name)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }

    override suspend fun getTasksByPriority(call: ApplicationCall) {
        val priorityAsText = call.parameters["priority"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        try {
            val priority = Priority.valueOf(priorityAsText)
            val tasks = taskService.tasksByPriority(priority)

            if (tasks.isEmpty()) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(tasks)
            }
        } catch (ex: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    override suspend fun createTask(call: ApplicationCall) {
        try {
            val task = call.receive<CreateTaskCommand>()
            taskService.addTask(task)
            call.respond(HttpStatusCode.NoContent)
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    override suspend fun updateTaskStatus(call: ApplicationCall) {
        val taskName = call.parameters["taskName"]
        val statusCode = call.parameters["statusCode"]

        if (taskName == null || statusCode == null) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        try {
            val success = taskService.updateTaskStatus(taskName, statusCode)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    override suspend fun deleteTask(call: ApplicationCall) {
        val name = call.parameters["taskName"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        if (taskService.removeTask(name)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}