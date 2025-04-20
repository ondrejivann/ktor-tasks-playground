package infrastructure.rest.route

import infrastructure.rest.controller.TaskController
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.taskRoutes(taskController: TaskController) {
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
