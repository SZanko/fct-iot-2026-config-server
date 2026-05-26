package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "config")
class IotConfigModel(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,
    var name: String,
    var busStopId: String,
    var ldrLimit: Int,
    var temperature: Int,
    var latitude: Double? = null,
    var longitude: Double? = null,
)
