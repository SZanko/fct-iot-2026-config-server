package pt.nova.fct.iot.configserver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min

data class DemoArrivalAdjustRequest(
    @field:JsonProperty("deltaMinutes")
    val deltaMinutes: Int,  // positive = delay, negative = advance
)

data class DemoArrivalRequest(
    @field:JsonProperty("lineId")
    val lineId: String,

    @field:JsonProperty("headsign")
    val headsign: String,

    @field:Min(0)
    @field:JsonProperty("minutesAway")
    val minutesAway: Int,
)

data class DemoArrivalDto(
    @field:JsonProperty("busStopId")
    val busStopId: String,

    @field:JsonProperty("lineId")
    val lineId: String,

    @field:JsonProperty("headsign")
    val headsign: String,

    @field:JsonProperty("arrivalUnix")
    val arrivalUnix: Long,
)
