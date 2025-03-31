package infrastructure.graphql.queries

import domain.ports.driving.FileService
import infrastructure.graphql.model.files.FileExistenceResultGQL
import org.koin.core.annotation.Single

@Single
class FileStorageQueries(
    private val fileService: FileService,
) {
    suspend fun checkFileExists(fileKey: String): FileExistenceResultGQL {
        return try {
            val exists = fileService.checkFileExists(fileKey)
            FileExistenceResultGQL(
                exists = exists,
                fileKey = fileKey
            )
        } catch (e: Exception) {
            FileExistenceResultGQL(
                exists = false,
                fileKey = fileKey,
                errorMessage = e.message
            )
        }
    }
}