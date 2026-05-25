package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "config")
class IotConfigModel(
    @field:Id
    var id : String,
    var busStopId: String,
    var ldrLimit: Int,
    var temperature: Int,
)
