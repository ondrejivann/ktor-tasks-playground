package infrastructure.rest.route

import infrastructure.rest.controller.TaskController
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Route.taskRoutes(
    taskController: TaskController,
) {
    staticResources("static", "static")

    route("/tasks") {
        get {
            taskController.getAllTasks(call)
        }

        get("/byName/{taskName}") {
            taskController.getTaskByName(call)
        }

        get("/byPriority/{priority}") {
            taskController.getTasksByPriority(call)
        }

        post {
            taskController.createTask(call)
        }

        put("/{taskName}/status/{statusCode}") {
            taskController.updateTaskStatus(call)
        }

        delete("/{taskName}") {
            taskController.deleteTask(call)
        }
    }
}
