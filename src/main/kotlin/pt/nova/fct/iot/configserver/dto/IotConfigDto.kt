package pt.nova.fct.iot.configserver.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class IotConfigDto(
    val id: String, // Same as the bus stop Id from the carris api
    val name: String,
    @field:Min(0)
    @field:Max(255)
    val light: Int,
    @field:Min(-20)
    @field:Max(60)
    val temperature: Int,
)
