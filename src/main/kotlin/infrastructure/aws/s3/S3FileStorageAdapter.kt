package infrastructure.aws.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import domain.ports.FileStoragePort
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.time.Duration
import kotlin.time.toKotlinDuration

@Single(binds = [FileStoragePort::class])
class S3FileStorageAdapter(config: ApplicationConfig) : FileStoragePort {

    private val region = config.property(S3Config.AWS_REGION_PATH).getString()
    private val bucketName = config.property(S3Config.AWS_BUCKET_NAME_PATH).getString()
    private val urlExpirationSeconds = config.property(S3Config.AWS_URL_EXPIRATION_SECONDS_PATH).getString().toLong()

    override suspend fun generateUploadUrl(key: String, contentType: String, metadata: Map<String, String>): String {
        return withContext(Dispatchers.IO) {
            S3Client.fromEnvironment { region = this@S3FileStorageAdapter.region }.use { s3Client ->
                val request = PutObjectRequest {
                    this.bucket = bucketName
                    this.key = key
                    this.contentType = contentType
                    this.metadata = metadata
                }
                val preSignedRequest = s3Client.presignPutObject(
                    input = request,
                    duration = Duration.ofSeconds(urlExpirationSeconds).toKotlinDuration()
                )
                preSignedRequest.url.toString()
            }
        }
    }

    override suspend fun generateDownloadUrl(key: String): String {
        return withContext(Dispatchers.IO) {
            S3Client.fromEnvironment { region = this@S3FileStorageAdapter.region }.use { s3Client ->
                val request = GetObjectRequest {
                    this.bucket = bucketName
                    this.key = key
                }
                val preSignedRequest = s3Client.presignGetObject(
                    input = request,
                    duration = Duration.ofSeconds(urlExpirationSeconds).toKotlinDuration()
                )
                preSignedRequest.url.toString()
            }
        }
    }

    override suspend fun objectExists(key: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                S3Client.fromEnvironment { region = this@S3FileStorageAdapter.region }.use { s3Client ->
                    s3Client.headObject(HeadObjectRequest {
                        this.bucket = bucketName
                        this.key = key
                    })
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}