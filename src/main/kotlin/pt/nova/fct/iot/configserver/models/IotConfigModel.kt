package pt.nova.fct.iot.configserver.models

import org.springframework.data.annotation.Id

data class IotConfigModel(
    @field:Id
    val id : String,
    val busStopId: String,
    val ldrLimit: Int,
)
