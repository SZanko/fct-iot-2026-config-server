package pt.nova.fct.iot.configserver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class IotConfigDto(
    @field:JsonProperty("id")
    val id: String,
    @field:JsonProperty("name")
    val name: String,
    @field:Min(0)
    @field:Max(255)
    @field:JsonProperty("light")
    val light: Int,
    @field:Min(-20)
    @field:Max(60)
    @field:JsonProperty("temperature")
    val temperature: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val buzzerEnabled: Boolean = false,
    val buzzerType: String = "single",
    val buzzerDurationMs: Int = 200,
    val buzzerIntervalMs: Int = 1000,
    val lightsEnabled: Boolean = false,
    val buzzerStages: List<BuzzerStageDto> = emptyList(),
)
