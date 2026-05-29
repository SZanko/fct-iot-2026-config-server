package pt.nova.fct.iot.configserver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

data class BuzzerStageDto(
    @field:JsonProperty("id")
    val id: String? = null,

    @field:Min(0)
    @field:JsonProperty("minutesBefore")
    val minutesBefore: Int,

    @field:Pattern(regexp = "single|double|triple|sos|continuous")
    @field:JsonProperty("buzzerType")
    val buzzerType: String,

    @field:Min(50)
    @field:JsonProperty("buzzerDurationMs")
    val buzzerDurationMs: Int,
)
