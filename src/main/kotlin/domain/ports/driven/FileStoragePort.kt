package domain.ports.driven

interface FileStoragePort {
    suspend fun generateUploadUrl(key: String, contentType: String, metadata: Map<String, String> = emptyMap()): String

    suspend fun generateDownloadUrl(key: String): String

    suspend fun objectExists(key: String): Boolean

    suspend fun deleteObject(key: String): Boolean
}
