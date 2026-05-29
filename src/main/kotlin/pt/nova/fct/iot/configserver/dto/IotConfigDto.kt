package pt.nova.fct.iot.configserver.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class IotConfigDto(
    @field:JsonProperty("id")
    val id: String, // Same as the bus stop Id from the carris api
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
)
