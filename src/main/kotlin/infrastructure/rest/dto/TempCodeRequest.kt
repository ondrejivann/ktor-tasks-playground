package infrastructure.rest.dto

import kotlinx.serialization.Serializable

@Serializable
data class TempCodeRequest(val code: String)