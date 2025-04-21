package infrastructure.graphql.queries

import domain.ports.driving.FileService
import infrastructure.graphql.model.files.FileExistenceResultGQL
import org.koin.core.annotation.Single

@Single
class FileStorageQueries(private val fileService: FileService) {
    suspend fun checkFileExists(fileKey: String): FileExistenceResultGQL {
        val exists = fileService.checkFileExists(fileKey)
        return FileExistenceResultGQL(
            exists = exists,
            fileKey = fileKey,
        )
    }
}
