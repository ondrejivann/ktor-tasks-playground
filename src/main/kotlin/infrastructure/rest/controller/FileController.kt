package infrastructure.rest.controller

import io.ktor.server.application.ApplicationCall

interface FileController {
    suspend fun prepareFileUpload(call: ApplicationCall)
    suspend fun getDownloadLink(call: ApplicationCall)
    suspend fun checkFileExists(call: ApplicationCall)
    suspend fun removeFile(call: ApplicationCall)
}
