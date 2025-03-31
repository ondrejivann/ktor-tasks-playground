package infrastructure.aws.s3

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import domain.ports.driven.FileStoragePort
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.time.Duration
import kotlin.time.toKotlinDuration

@Single(binds = [FileStoragePort::class])
class S3FileStorageAdapter(
    config: ApplicationConfig,
    private val s3ClientFactory: S3ClientFactory,
) : FileStoragePort {
    private val logger = KotlinLogging.logger { }

    private val region = config.property(S3Config.AWS_REGION_PATH).getString()
    private val bucketName = config.property(S3Config.AWS_BUCKET_NAME_PATH).getString()
    private val urlExpirationSeconds = config.property(S3Config.AWS_URL_EXPIRATION_SECONDS_PATH).getString().toLong()

    override suspend fun generateUploadUrl(key: String, contentType: String, metadata: Map<String, String>): String {
        return withContext(Dispatchers.IO) {
            s3ClientFactory.createClient(region).use { s3Client ->
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
            s3ClientFactory.createClient(region).use { s3Client ->
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
                s3ClientFactory.createClient(region).use { s3Client ->
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

    override suspend fun deleteObject(key: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                s3ClientFactory.createClient(region).use { s3Client ->
                    s3Client.deleteObject(
                        DeleteObjectRequest {
                            this.bucket = bucketName
                            this.key = key
                        }
                    )
                }
            }
            true
        } catch (e: Exception) {
            logger.error { "Failed to delete object with key $key: ${e.message}" }
            false
        }
    }
}

interface S3ClientFactory {
    fun createClient(region: String): S3Client
}

@Single(binds = [S3ClientFactory::class])
class ConfigBasedS3ClientFactory(private val config: ApplicationConfig) : S3ClientFactory {
    private val accessKey = config.property(S3Config.AWS_ACCESS_KEY_PATH).getString()
    private val secretKey = config.property(S3Config.AWS_SECRET_KEY_PATH).getString()

    override fun createClient(region: String): S3Client {
        return S3Client {
            this.region = region
            this.credentialsProvider = StaticCredentialsProvider {
                accessKeyId = accessKey
                secretAccessKey = secretKey
            }
        }
    }
}