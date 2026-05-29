package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "environment_reading")
class IotEnvironmentModel(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @field:Column(nullable = false, unique = true)
    var busStopId: String,

    @field:Column(nullable = false)
    var temperature: Double,

    @field:Column(nullable = false)
    var darkOutside: Boolean,

    @field:Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
)
