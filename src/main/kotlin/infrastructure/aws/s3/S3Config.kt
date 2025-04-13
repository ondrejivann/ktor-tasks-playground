package infrastructure.aws.s3

/**
 * Konfigurační konstanty pro AWS S3 adaptér
 */
object S3Config {

    // Konfigurační cesty
    const val AWS_REGION_PATH = "ktor.aws.s3.region"
    const val AWS_BUCKET_NAME_PATH = "ktor.aws.s3.bucketName"
    const val AWS_ACCESS_KEY_PATH = "ktor.aws.s3.accessKey"
    const val AWS_SECRET_KEY_PATH = "ktor.aws.s3.secretKey"
    const val AWS_URL_EXPIRATION_SECONDS_PATH = "ktor.aws.s3.urlExpirationSeconds"
    
    // Výchozí hodnoty (volitelné)
    const val DEFAULT_URL_EXPIRATION_SECONDS = 3600L
    
    // Konstanty pro S3 operace
    const val UPLOADS_PREFIX = "taskimages/"
    const val METADATA_ORIGINAL_FILENAME = "originalFileName"
    const val METADATA_UPLOADED_BY = "uploadedBy"
    const val METADATA_UPLOADED_AT = "uploadedAt"
} 