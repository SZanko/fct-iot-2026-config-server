package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "config")
data class IotConfigModel(
    @field:Id
    val id : String,
    val busStopId: String,
    val ldrLimit: Int,
)
