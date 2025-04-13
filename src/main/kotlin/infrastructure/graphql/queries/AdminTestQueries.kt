package infrastructure.graphql.queries

import org.koin.core.annotation.Single

@Single
class AdminTestQueries(
    private val fileStorageQueries: FileStorageQueries
) {
    fun fileStorage(): FileStorageQueries = fileStorageQueries
}