package pt.nova.fct.iot.configserver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import java.time.Instant

data class IotEnvironmentRequest(
    @field:DecimalMin("-50.0")
    @field:DecimalMax("80.0")
    @field:JsonProperty("temperature")
    val temperature: Double,

    @field:JsonProperty("darkOutside")
    val darkOutside: Boolean,
)

data class IotEnvironmentDto(
    @field:JsonProperty("id")
    val id: String,

    @field:JsonProperty("temperature")
    val temperature: Double,

    @field:JsonProperty("darkOutside")
    val darkOutside: Boolean,

    @field:JsonProperty("updatedAt")
    val updatedAt: Instant,
)
