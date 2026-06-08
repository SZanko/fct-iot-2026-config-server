package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "demo_arrival")
class DemoArrivalModel(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @field:Column(nullable = false, unique = true)
    var busStopId: String,

    @field:Column(nullable = false)
    var lineId: String,

    @field:Column(nullable = false)
    var headsign: String,

    // Original scheduled arrival set at creation — never changes (used as stable alarm key)
    @field:Column(nullable = false)
    var scheduledArrivalUnix: Long,

    // Current estimated arrival — updated on every adjust call
    @field:Column(nullable = false)
    var arrivalUnix: Long,
)
