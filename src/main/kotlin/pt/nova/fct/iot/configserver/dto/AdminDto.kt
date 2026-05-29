package pt.nova.fct.iot.configserver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

data class AdminStopDto(
    @field:JsonProperty("stopId") val stopId: String,
    @field:JsonProperty("deviceName") val deviceName: String,
    @field:JsonProperty("temperature") val temperature: Double,
    @field:JsonProperty("darkOutside") val darkOutside: Boolean,
    @field:JsonProperty("isActive") val isActive: Boolean,
    @field:JsonProperty("lastSeenSeconds") val lastSeenSeconds: Long,
    @field:JsonProperty("buzzerEnabled") val buzzerEnabled: Boolean,
    @field:JsonProperty("buzzerType") val buzzerType: String,
    @field:JsonProperty("buzzerDurationMs") val buzzerDurationMs: Int,
    @field:JsonProperty("buzzerIntervalMs") val buzzerIntervalMs: Int,
    @field:JsonProperty("lightsEnabled") val lightsEnabled: Boolean,
    @field:JsonProperty("ldrThreshold") val ldrThreshold: Int,
    @field:JsonProperty("demo") val demo: DemoArrivalDto?,
    @field:JsonProperty("buzzerStages") val buzzerStages: List<BuzzerStageDto> = emptyList(),
)

data class StopConfigRequest(
    @field:JsonProperty("buzzerEnabled") val buzzerEnabled: Boolean,
    @field:Pattern(regexp = "single|double|triple|sos|continuous")
    @field:JsonProperty("buzzerType") val buzzerType: String,
    @field:Min(50)
    @field:JsonProperty("buzzerDurationMs") val buzzerDurationMs: Int,
    @field:Min(100)
    @field:JsonProperty("buzzerIntervalMs") val buzzerIntervalMs: Int,
    @field:JsonProperty("lightsEnabled") val lightsEnabled: Boolean,
    @field:Min(0) @field:jakarta.validation.constraints.Max(4095)
    @field:JsonProperty("ldrThreshold") val ldrThreshold: Int,
)
