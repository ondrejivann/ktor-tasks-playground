package infrastructure.graphql.model.files

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Input for preparing a file upload")
data class FileUploadInputGQL(val fileName: String, val contentType: String, val fileSize: Int)
