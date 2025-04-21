package infrastructure.rest.route

import infrastructure.rest.controller.FileController
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.fileRoutes(fileController: FileController) {
    route("/files") {
        post("/prepare-upload") {
            fileController.prepareFileUpload(call)
        }

        post("/download") {
            fileController.getDownloadLink(call)
        }

        get("/exists/{fileKey}") {
            fileController.checkFileExists(call)
        }
        post("/remove") {
            fileController.removeFile(call)
        }
    }
}
