package infrastructure.rest.route

import infrastructure.rest.controller.FileController
import io.ktor.server.routing.*

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
    }
}