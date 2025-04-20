package infrastructure.graphql.mutations

import org.koin.core.annotation.Single

@Single
class AdminTestMutations(private val fileStorageMutations: FileStorageMutations) {
    fun fileStorage(): FileStorageMutations = fileStorageMutations
}
